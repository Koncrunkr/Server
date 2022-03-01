package ru.comgrid.server.api.table;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import ru.comgrid.server.api.user.AccessService;
import ru.comgrid.server.api.user.UserHelp;
import ru.comgrid.server.exception.IllegalAccessException;
import ru.comgrid.server.exception.WrongRequestException;
import ru.comgrid.server.model.Chat;
import ru.comgrid.server.model.Person;
import ru.comgrid.server.model.Right;
import ru.comgrid.server.model.TableParticipants;
import ru.comgrid.server.repository.CellUnionRepository;
import ru.comgrid.server.repository.ChatParticipantsRepository;
import ru.comgrid.server.repository.ChatRepository;
import ru.comgrid.server.repository.PersonRepository;
import ru.comgrid.server.util.EnumSet0;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Table service, that has most commonly used table targeted endpoints.
 * (frontend must not specify any credentials since
 * it is done automatically if person is authorized)
 * @author MediaNik
 */
@RestController
@RequestMapping(value = "/table", produces = "application/json; charset=utf-8")
public class TableService{

    private final ChatRepository chatRepository;
    private final ChatParticipantsRepository participantsRepository;
    private final PersonRepository personRepository;

    private final AccessService accessService;
    private final int defaultPageSize;

    /**
     * @hidden
     */
    public TableService(
        @Autowired ChatRepository chatRepository,
        @Autowired ChatParticipantsRepository participantsRepository,
        @Autowired PersonRepository personRepository,
        @Autowired AccessService accessService,
        @Autowired CellUnionRepository cellUnionRepository,
        @Value("${ru.comgrid.chat.participants.default-page-size}") int defaultPageSize
    ){
        this.chatRepository = chatRepository;
        this.participantsRepository = participantsRepository;
        this.personRepository = personRepository;
        this.accessService = accessService;
        this.defaultPageSize = defaultPageSize;
    }

    /**
     * Create the table with specified parameters:
     * <pre>
        | param           | includes | description                |
        |-----------------|----------|----------------------------|
        | name: string    | always   | name of chat               |
        | creator: string | never    | chat's creator's unique id |
        | width: integer  | always   | width of chat in cells     |
        | height: integer | always   | height of chat in cells    |
        | avatar: string  | always   | link to avatar of chat     |
     * </pre>
     * <i>Note: you should not include creator's id, because authenticated user's will be used</i>
     *
     * @param user implementation specific user info
     * @param chat chat object accommodating all parameters
     * @return Created {@link Chat} object in json format
     */
    @PostMapping("/create")
    public ResponseEntity<Chat> createTable(
        @AuthenticationPrincipal(errorOnInvalidType = true) OAuth2User user,
        @RequestBody Chat chat
    ){
        var userId = UserHelp.extractId(user);
        chat.setId(null);
        chat.setCreator(UserHelp.extractId(user));
        chat.setCreated(LocalDateTime.now(Clock.systemUTC()));
        chat = chatRepository.save(chat);
        participantsRepository.save(new TableParticipants(chat.getId(), userId, EnumSet0.allOf(Right.class), LocalDateTime.now(Clock.systemUTC())));
        return ResponseEntity.ok(chat);
    }

    /**
     * <p>Get information about table with specified parameters:</p>
     * <pre>
       | param                        | includes | description                                   |
       |------------------------------|----------|-----------------------------------------------|
       | id: integer                  | always   | unique id of table(chat)                      |
       | includeParticipants: boolean | optional | whether to include participants({@link Person}) or not |
     * </pre>
     *
     * @param user Authenticated user from Spring security
     * @param chatId id of chat, that you want to know info about
     * @param includeParticipants whether to include participants({@link Person}) or not
     * @return {@link Chat} in json format
     */
    @GetMapping("/info")
    public ResponseEntity<Chat> infoAboutTable(
        @AuthenticationPrincipal OAuth2User user,
        @RequestParam long chatId,
        @RequestParam(required = false, defaultValue = "false") boolean includeParticipants
    ){
        var userId = UserHelp.extractId(user);
        if(!participantsRepository.existsByChatAndPerson(chatId, userId))
            return ResponseEntity.notFound().build();

        @SuppressWarnings("OptionalGetWithoutIsPresent") // We know it, because it is in participantsRepository
        Chat chat = chatRepository.findById(chatId).get();

        if(includeParticipants){
            List<BigDecimal> personIds = participantsRepository.findAllByChat(
                chatId,
                Pageable.ofSize(defaultPageSize)
            ).getContent();
            Iterable<Person> participants = personRepository.findAllById(personIds);
            chat.setParticipants(participants);
        }

        return ResponseEntity.ok(chat);
    }

    @ApiResponse(
        responseCode = "400",
        description = "Bad request. Error codes: user.not_found, user.already_participant",
        content = @Content()
    )
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden access. Error codes: add_participant",
        content = @Content()
    )
    @ApiResponse(
        responseCode = "200",
        description = "User was added",
        content = @Content()
    )
    @PostMapping("/add_participant")
    public void addParticipant(
        @AuthenticationPrincipal OAuth2User user,
        @RequestBody AddParticipantRequest addParticipantRequest
    ){
        var adminUserId = UserHelp.extractId(user);
        var newUserId = new BigDecimal(addParticipantRequest.userId);

        if (!accessService.hasAccessTo(adminUserId, addParticipantRequest.chatId, Right.AddUsers)){
            throw new IllegalAccessException("add_participant");
        }

        if(!personRepository.existsById(newUserId)){
            throw new WrongRequestException("user.not_found");
        }

        if(participantsRepository.existsByChatAndPerson(addParticipantRequest.chatId, newUserId)){
            throw new WrongRequestException("user.already_participant");
        }

        participantsRepository.save(new TableParticipants(
            addParticipantRequest.chatId,
            newUserId,
            EnumSet0.of(Right.Read),
            LocalDateTime.now()
        ));
    }


    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class AddParticipantRequest{
        long chatId;
        String userId;
    }
}






