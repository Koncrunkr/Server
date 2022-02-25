package ru.comgrid.server.api.table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import ru.comgrid.server.api.message.MessageUnionRequest;
import ru.comgrid.server.api.message.MessagesRequest;
import ru.comgrid.server.api.message.MessageService;
import ru.comgrid.server.api.user.AccessService;
import ru.comgrid.server.api.user.UserHelp;
import ru.comgrid.server.model.*;
import ru.comgrid.server.repository.ChatParticipantsRepository;
import ru.comgrid.server.repository.ChatRepository;
import ru.comgrid.server.repository.MessageUnionRepository;
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
@RequestMapping(value = "/table", produces = "application/json")
public class TableService{

    private final ChatRepository chatRepository;
    private final ChatParticipantsRepository participantsRepository;
    private final PersonRepository personRepository;
    private final MessageService messageService;
    private final AccessService accessService;
    private final MessageUnionRepository messageUnionRepository;
    private final int defaultPageSize;
    private final int maxMessagesSize;

    /**
     * @hidden
     */
    public TableService(
        @Autowired ChatRepository chatRepository,
        @Autowired ChatParticipantsRepository participantsRepository,
        @Autowired PersonRepository personRepository,
        @Autowired MessageService messageService,
        @Autowired AccessService accessService,
        @Autowired MessageUnionRepository messageUnionRepository,
        @Value("${ru.comgrid.chat.participants.default-page-size}") int defaultPageSize,
        @Value("${ru.comgrid.chat.messages.max}") int maxMessagesSize
    ){
        this.chatRepository = chatRepository;
        this.participantsRepository = participantsRepository;
        this.personRepository = personRepository;
        this.messageService = messageService;
        this.accessService = accessService;
        this.messageUnionRepository = messageUnionRepository;
        this.defaultPageSize = defaultPageSize;
        this.maxMessagesSize = maxMessagesSize;
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
    public ResponseEntity<String> createTable(
        @AuthenticationPrincipal(errorOnInvalidType = true) OAuth2User user,
        @RequestBody Chat chat
    ){
        var userId = UserHelp.extractId(user);
        chat.setId(null);
        chat.setCreator(UserHelp.extractId(user));
        chat.setCreated(LocalDateTime.now(Clock.systemUTC()));
        chat = chatRepository.save(chat);
        participantsRepository.save(new TableParticipants(chat.getId(), userId, EnumSet0.allOf(Right.class), LocalDateTime.now(Clock.systemUTC())));
        return ResponseEntity.ok(chat.toString());
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
    public ResponseEntity<String> infoAboutTable(
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

        return ResponseEntity.ok(chat.toString());
    }

    /**
     * Get messages in square, that has top left corner to be in (xCoordLeftTop, yCoordLeftTop) point
     * and bottom right corner to be in (xCoordBottomRight, yCoordBottomRight) point.
     * Parameters:
     * <pre>
     | param               | includes | description                                                                  |
     |---------------------|----------|------------------------------------------------------------------------------|
     | chatId              | optional | unique chatId                                                                |
     | xCoordLeftTop       | optional | Top left point of square's x coord(default 0)                                |
     | yCoordLeftTop       | optional | Top left point of square's y coord(default 0)                                |
     | xCoordRightBottom   | optional | Bottom right point of square's x coord(default width-1)                      |
     | yCoordRightBottom   | optional | Bottom right point of square's y coord(default height-1)                     |
     | amountOfMessages    | optional | Amount of messages that will be loaded(maximum available 100, default is 50) |
     | sinceDateTimeMillis | optional | Minimum time of messages to include(default no limit)                        |
     | untilDateTimeMillis | optional | Maximum time of messages to include(default no limit)                        |
     * </pre>
     * @param user Authenticated user from Spring security
     * @param messagesRequest chat object accommodating all parameters
     * @return {@link Message} list in json format
     */
    // using post, because it can have a lot of information, that get request might not accommodate
    @PostMapping("/messages")
    public ResponseEntity<String> getMessages(
        @AuthenticationPrincipal OAuth2User user,
        @RequestBody MessagesRequest messagesRequest
    ){
        var userId = UserHelp.extractId(user);

        if(!accessService.hasAccessTo(userId, messagesRequest.chatId, Right.Read))
//            throw new IllegalAccessException("to read messages in this chat");
            return ResponseEntity.status(403).body("{\"timestamp\": \"" + LocalDateTime.now() + "\", \"status\": 403, \"reason\": \"Sorry, you don't have access to read messages in this chat\"}");

        if(messagesRequest.amountOfMessages > maxMessagesSize)
//            throw new TooBigRequestException("messages", maxMessagesSize);
            return ResponseEntity.badRequest().body("{\"timestamp\": \"" + LocalDateTime.now() + "\", \"status\": 400, \"reason\": \"Amount of messages has to be not greater than " + maxMessagesSize + "\"}");

        if(messagesRequest.amountOfMessages < 0)
//            throw new WrongRequestException("Amount of messages has to be positive");
            return ResponseEntity.badRequest().body("{\"timestamp\": \"" + LocalDateTime.now() + "\", \"status\": 400, \"reason\": \"Amount of messages has to be positive\"}");

        if(messagesRequest.amountOfMessages == 0)
            messagesRequest.amountOfMessages = defaultPageSize;

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        Chat chat = chatRepository.findById(messagesRequest.chatId).get();

        if(TableHelp.checkBorders(chat, messagesRequest)){
//            throw new OutOfBoundsRequestException();
            return ResponseEntity.badRequest().body("{\"timestamp\": \"" + LocalDateTime.now() + "\", \"status\": 400, \"reason\": \"You are out of borders\"}");
        }

        if(TableHelp.checkTimeBorders(messagesRequest)){
//            throw new OutOfBoundsRequestException("You can't specify neither negative time nor future");
            return ResponseEntity.badRequest().body("{\"timestamp\": \"" + LocalDateTime.now() + "\", \"status\": 400, \"reason\": \"You can't specify neither negative time nor future\"}");
        }

        if(messagesRequest.sinceDateTimeMillis == 0 && messagesRequest.untilDateTimeMillis == 0){
            // neither since nor until are specified, do fast getLastMessages
            return messageService.getLastMessages(messagesRequest);
        }else if(messagesRequest.sinceDateTimeMillis == 0){
            // only until is specified
            return messageService.getMessagesUntil(messagesRequest);
        }else if(messagesRequest.untilDateTimeMillis == 0){
            // only since is specified
            return messageService.getMessagesSince(messagesRequest);
        }else{
            // specified both since and until
            return messageService.getMessagesBetween(messagesRequest);
        }
    }

    @PostMapping("/add_participant")
    public ResponseEntity<String> addParticipant(
        @AuthenticationPrincipal OAuth2User user,
        @RequestBody AddParticipantRequest addParticipantRequest
    ){
        var adminUserId = UserHelp.extractId(user);
        var newUserId = new BigDecimal(addParticipantRequest.userId);

        if (!accessService.hasAccessTo(adminUserId, addParticipantRequest.chatId, Right.AddUsers)){
            return ResponseEntity.status(403).body("You don't have access to add participants to this chat");
        }

        if(!personRepository.existsById(newUserId)){
            return ResponseEntity.badRequest().body("User is not found");
        }

        if(participantsRepository.existsByChatAndPerson(addParticipantRequest.chatId, newUserId)){
            return ResponseEntity.badRequest().body("User is already a participant of this chat");
        }

        participantsRepository.save(new TableParticipants(
            addParticipantRequest.chatId,
            newUserId,
            EnumSet0.of(Right.Read),
            LocalDateTime.now()
        ));

        return ResponseEntity.ok("{response: \"User was added successfully\"}");
    }

    @GetMapping("/cell_unions")
    public ResponseEntity<String> cellUnions(
        @AuthenticationPrincipal OAuth2User user,
        @RequestParam long chatId,
        @RequestParam(required = false, defaultValue = "0") int xCoordLeftTop,
        @RequestParam(required = false, defaultValue = "0") int yCoordLeftTop,
        @RequestParam(required = false, defaultValue = "0") int xCoordRightBottom,
        @RequestParam(required = false, defaultValue = "0") int yCoordRightBottom
    ){
        var userId = UserHelp.extractId(user);

        if(!accessService.hasAccessTo(userId, chatId, Right.Read)){
            return ResponseEntity.status(403).body("{\"timestamp\": \"" + LocalDateTime.now() + "\", \"status\": 403, \"reason\": \"Sorry, you don't have access to read messages in this chat\"}");
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        Chat chat = chatRepository.findById(chatId).get();

        if(TableHelp.checkBorders(chat, new MessageUnionRequest(chatId, xCoordLeftTop, yCoordLeftTop, xCoordRightBottom, yCoordRightBottom))){
            return ResponseEntity.badRequest().body("{\"timestamp\": \"" + LocalDateTime.now() + "\", \"status\": 400, \"reason\": \"You are out of borders\"}");
        }

        return ResponseEntity.ok(
            TableHelp.toJson(messageUnionRepository.findAllByChat(
                chatId,
                xCoordLeftTop,
                yCoordLeftTop,
                xCoordRightBottom,
                yCoordRightBottom,
                Pageable.ofSize(defaultPageSize)
            )).toString());
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






