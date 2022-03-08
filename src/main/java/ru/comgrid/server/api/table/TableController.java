package ru.comgrid.server.api.table;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TableController{

    private final ChatRepository chatRepository;
    private final ChatParticipantsRepository participantsRepository;
    private final PersonRepository personRepository;

    private final AccessService accessService;

    /**
     * @hidden
     */
    public TableController(
        @Autowired ChatRepository chatRepository,
        @Autowired ChatParticipantsRepository participantsRepository,
        @Autowired PersonRepository personRepository,
        @Autowired AccessService accessService
    ){
        this.chatRepository = chatRepository;
        this.participantsRepository = participantsRepository;
        this.personRepository = personRepository;
        this.accessService = accessService;
    }

    @Operation()
    @PostMapping("/create")
    public ResponseEntity<Chat> createTable(
        @AuthenticationPrincipal OAuth2User user,
        @RequestBody NewChat newChat
    ){
        var userId = UserHelp.extractId(user);
        Chat chat = new Chat(userId, newChat.name, newChat.width, newChat.height, newChat.avatar);
        chat.setCreated(LocalDateTime.now(Clock.systemUTC()));
        chat = chatRepository.save(chat);
        participantsRepository.save(new TableParticipants(chat.getId(), userId, EnumSet0.allOf(Right.class), LocalDateTime.now(Clock.systemUTC())));
        return ResponseEntity.ok(chat);
    }
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class NewChat{
        @Schema(defaultValue = "name")
        private String name;
        private int width;
        private int height;
        @Schema(defaultValue = "url")
        private String avatar;
    }


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
                chatId
            );
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
            EnumSet0.of(Right.Read, Right.SendMessages),
            LocalDateTime.now()
        ));
    }


    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class AddParticipantRequest{
        long chatId;
        @Schema(defaultValue = "314159265358979323846")
        String userId;
    }
}






