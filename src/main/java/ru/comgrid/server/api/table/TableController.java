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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.comgrid.server.api.user.AccessService;
import ru.comgrid.server.api.user.UserHelp;
import ru.comgrid.server.api.util.FileController;
import ru.comgrid.server.exception.IllegalAccessException;
import ru.comgrid.server.exception.OutOfBoundsRequestException;
import ru.comgrid.server.exception.RequestException;
import ru.comgrid.server.model.Chat;
import ru.comgrid.server.model.Person;
import ru.comgrid.server.model.Right;
import ru.comgrid.server.model.TableParticipants;
import ru.comgrid.server.repository.ChatParticipantsRepository;
import ru.comgrid.server.repository.ChatRepository;
import ru.comgrid.server.repository.PersonRepository;
import ru.comgrid.server.util.EnumSet0;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
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
    private final FileController fileController;

    private final AccessService accessService;

    /**
     * @hidden
     */
    public TableController(
            @Autowired ChatRepository chatRepository,
            @Autowired ChatParticipantsRepository participantsRepository,
            @Autowired PersonRepository personRepository,
            @Autowired FileController fileController,
            @Autowired AccessService accessService
    ){
        this.chatRepository = chatRepository;
        this.participantsRepository = participantsRepository;
        this.personRepository = personRepository;
        this.fileController = fileController;
        this.accessService = accessService;
    }

//    @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "multipart/form-data")))
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<Chat> createTable(
        @AuthenticationPrincipal OAuth2User user,
        @ModelAttribute @Valid NewChat newChat
    ){
        FileController.ImageEntity imageEntity = fileController.uploadImage(newChat.avatarFile, newChat.avatarLink);
        var userId = UserHelp.extractId(user);
        checkBorders(newChat.width, newChat.height);
        Chat chat = new Chat(userId, newChat.name, newChat.width, newChat.height, imageEntity.getUrl());
        chat.setCreated(LocalDateTime.now(Clock.systemUTC()));
        chat = chatRepository.save(chat);
        participantsRepository.save(new TableParticipants(chat.getId(), userId, EnumSet0.allOf(Right.class), LocalDateTime.now(Clock.systemUTC())));
        return ResponseEntity.ok(chat);
    }

    private void checkBorders(int width, int height){
        if(width <= 0 || height <= 0)
            throw new OutOfBoundsRequestException("negative_size");
        if(width * height > 2500)
            throw new OutOfBoundsRequestException("too_large");
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class NewChat implements Serializable {
        @NotEmpty private String name;
        @NotNull private int width;
        @NotNull private int height;
        @Schema(nullable = true) private MultipartFile avatarFile;
        @Schema(nullable = true) private String avatarLink;
    }


    @ApiResponse(description = "Chat not found", responseCode = "404")
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
    @Transactional
    public void addParticipant(
        @AuthenticationPrincipal OAuth2User user,
        @Valid @RequestBody AddParticipantRequest addParticipantRequest
    ){
        var adminUserId = UserHelp.extractId(user);
        var newUserId = new BigDecimal(addParticipantRequest.userId);

        if (!accessService.hasAccessTo(adminUserId, addParticipantRequest.chatId, Right.AddUsers)){
            throw new IllegalAccessException("add_participant");
        }

        if(!personRepository.existsById(newUserId)){
            throw new RequestException(404, "user.not_found");
        }

        if(participantsRepository.existsByChatAndPerson(addParticipantRequest.chatId, newUserId)){
            throw new RequestException(404, "user.already_participant");
        }

        participantsRepository.save(new TableParticipants(
            addParticipantRequest.chatId,
            newUserId,
            EnumSet0.of(Right.Read, Right.SendMessages, Right.EditOwnMessages, Right.CreateCellUnions, Right.EditOwnCellUnions),
            LocalDateTime.now()
        ));
    }

    @PostMapping("/rights")
    @Transactional
    public void changeRights(
        @AuthenticationPrincipal OAuth2User user,
        @Valid @RequestBody ChangeRightsRequest changeRightsRequest
    ){
        var adminUserId = UserHelp.extractId(user);
        var newUserId = new BigDecimal(changeRightsRequest.userId);

        if(!accessService.hasAccessTo(adminUserId, changeRightsRequest.chatId, Right.ManageUsers)){
            throw new IllegalAccessException("manage_users");
        }

        if(!personRepository.existsById(newUserId)){
            throw new RequestException(404, "user.not_found");
        }

        TableParticipants participant = participantsRepository.findByChatAndPerson(changeRightsRequest.chatId, newUserId);

        if(participant == null){
            throw new RequestException(404, "user.not_in_chat");
        }

        participant.setRights(changeRightsRequest.getRights());

        participantsRepository.save(participant);
    }


    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class AddParticipantRequest{
        @NotNull long chatId;
        @Schema(defaultValue = "314159265358979323846")
        @NotEmpty String userId;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ChangeRightsRequest{
        @NotNull long chatId;
        @Schema(defaultValue = "314159265358979323846")
        @NotEmpty String userId;
        @Schema(defaultValue = "3")
        @NotNull long rights;
    }
}






