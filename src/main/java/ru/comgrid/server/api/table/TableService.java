package ru.comgrid.server.api.table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import ru.comgrid.server.api.user.UserHelp;
import ru.comgrid.server.model.*;
import ru.comgrid.server.repository.ChatParticipantsRepository;
import ru.comgrid.server.repository.ChatRepository;
import ru.comgrid.server.repository.MessageRepository;
import ru.comgrid.server.repository.PersonRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

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
    private final MessageRepository messageRepository;
    private final int defaultPageSize;
    private final int maxMessagesSize;

    /**
     * @hidden
     */
    public TableService(
        @Autowired ChatRepository chatRepository,
        @Autowired ChatParticipantsRepository participantsRepository,
        @Autowired PersonRepository personRepository,
        @Autowired MessageRepository messageRepository,
        @Value("${ru.comgrid.chat.participants.default-page-size}") int defaultPageSize,
        @Value("${ru.comgrid.chat.messages.max}") int maxMessagesSize
    ){
        this.chatRepository = chatRepository;
        this.participantsRepository = participantsRepository;
        this.personRepository = personRepository;
        this.messageRepository = messageRepository;
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
     * <pre>
Example:
//js
fetch(
     "/table/create",
    {
        method: "POST",
        credentials: "include", // compulsory
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            "name": name, // string
            "avatar": avatar, // string link
            "width": width, // int
            "height": height // int
        })
    }
).then(
    response => response.text()
).then(
    html => console.log(html)
)
     * </pre>
     * @param user implementation specific user info
     * @param chat chat object accommodating all parameters
     * @return Created {@link Chat} object in json format
     */
    @PostMapping("/create")
    public ResponseEntity<String> createTable(
        @AuthenticationPrincipal OAuth2User user,
        @RequestBody Chat chat
    ){
        var userId = UserHelp.extractId(user);
        chat.setId(null);
        chat.setCreator(UserHelp.extractId(user));
        chat.setCreated(LocalDateTime.now(Clock.systemUTC()));
        chat = chatRepository.save(chat);
        participantsRepository.save(new TableParticipants(chat.getId(), userId, EnumSet.allOf(Right.class), LocalDateTime.now(Clock.systemUTC())));
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
     * <pre>
     * Example:
       fetch(
           "https://comgrid.ru:8443/table/info?chatId=" + chatId + "&includeParticipants=true",
           {
               method: "GET",
               credentials: "include", //compulsory
               headers: {"Content-Type": "application/json"}
           }
       ).then(
           response => response.text()
       ).then(
           html => console.log(html)
       )

       ###

       fetch(
           "https://comgrid.ru:8443/table/info?chatId=" + chatId,
           {
               method: "GET",
               credentials: "include", // compulsory
           headers: {"Content-Type": "application/json"}
           }
       ).then(
           response => response.text()
       ).then(
           html => console.log(html)
       )
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
     | chatId              | always   | unique chatId                                                                |
     | xCoordLeftTop       | always   | Top left point of square's x coord                                           |
     | yCoordLeftTop       | always   | Top left point of square's y coord                                           |
     | xCoordRightBottom   | always   | Bottom right point of square's x coord                                       |
     | yCoordRightBottom   | always   | Bottom right point of square's y coord                                       |
     | amountOfMessages    | optional | Amount of messages that will be loaded(maximum available 100, default is 50) |
     | sinceDateTimeMillis | optional | Minimum time of messages to include(default no limit)                        |
     | untilDateTimeMillis | optional | Maximum time of messages to include(default no limit)                        |
     * </pre>
     * <pre>
Example:
// js
fetch(
     "/table/messages",
    {
        method: "POST",
        credentials: "include", // compulsory
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            "chatId": name, // int
            "xCoordLeftTop": square.topLeft.x, // int
            "yCoordLeftTop": square.topLeft.y, // int
            "xCoordRightBottom": square.rightBottom.x, // int
            "yCoordRightBottom": square.rightBottom.y, // int
            "amountOfMessages": amountOfMessages, // int
            "sinceDateTimeMillis": since.toMillis(), // int
            "untilDateTimeMillis": until.toMillis() // int
        })
    }
).then(
    response => response.text()
).then(
    html => console.log(html)
)
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

        Optional<TableParticipants> participance = participantsRepository.findById(new TableParticipant(
            messagesRequest.chatId,
            userId
        ));
        if(participance.isEmpty())
            return ResponseEntity.notFound().build();

        if(!participance.get().rights().contains(Right.Read))
            return ResponseEntity.status(403).body("Sorry, you don't have access to read messages in this chat");

        if(messagesRequest.amountOfMessages > maxMessagesSize)
            return ResponseEntity.badRequest().body("Amount of messages has to be not greater than " + maxMessagesSize);

        if(messagesRequest.amountOfMessages <= 0)
            return ResponseEntity.badRequest().body("Amount of messages has to be positive");

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        Chat chat = chatRepository.findById(messagesRequest.chatId).get();

        if(TableHelp.checkBorders(chat, messagesRequest)){
            return ResponseEntity.badRequest().body("You are out of borders");
        }

        if(TableHelp.checkTimeBorders(messagesRequest)){
            return ResponseEntity.badRequest().body("You can't specify neither negative time nor future");
        }

        if(messagesRequest.sinceDateTimeMillis == 0 && messagesRequest.untilDateTimeMillis == 0){
            // both not specified, do fast get last messages
            return getLastMessages(messagesRequest);
        }else if(messagesRequest.sinceDateTimeMillis == 0){
            // only until is specified
            return getMessagesUntil(messagesRequest);
        }else if(messagesRequest.untilDateTimeMillis == 0){
            // only since is specified
            return getMessagesSince(messagesRequest);
        }else{
            //specified both since and until
            return getMessagesBetween(messagesRequest);
        }
    }


    private ResponseEntity<String> getLastMessages(MessagesRequest messagesRequest){
        Page<Message> messages = messageRepository.findAllByChatIdAndXBetweenAndYBetweenOrderByTimeDesc(
            messagesRequest.chatId, messagesRequest.xCoordLeftTop, messagesRequest.xCoordRightBottom,
            messagesRequest.yCoordLeftTop, messagesRequest.yCoordRightBottom,
            Pageable.ofSize(messagesRequest.amountOfMessages)
        );

        return ResponseEntity.ok(TableHelp.toJson(messages).toString());
    }

    private ResponseEntity<String> getMessagesUntil(MessagesRequest messagesRequest){
        LocalDateTime until = TableHelp.toDateTime(messagesRequest.untilDateTimeMillis);

        Page<Message> messages = messageRepository.findAllByChatIdAndXBetweenAndYBetweenAndTimeBeforeOrderByTimeDesc(
            messagesRequest.chatId,
            messagesRequest.xCoordLeftTop,
            messagesRequest.xCoordRightBottom,
            messagesRequest.yCoordLeftTop,
            messagesRequest.yCoordRightBottom,
            until,
            Pageable.ofSize(messagesRequest.amountOfMessages)
        );

        return ResponseEntity.ok(TableHelp.toJson(messages).toString());
    }

    private ResponseEntity<String> getMessagesSince(MessagesRequest messagesRequest){
        LocalDateTime since = TableHelp.toDateTime(messagesRequest.sinceDateTimeMillis);

        Page<Message> messages = messageRepository.findAllByChatIdAndXBetweenAndYBetweenAndTimeAfterOrderByTimeDesc(
            messagesRequest.chatId,
            messagesRequest.xCoordLeftTop,
            messagesRequest.xCoordRightBottom,
            messagesRequest.yCoordLeftTop,
            messagesRequest.yCoordRightBottom,
            since,
            Pageable.ofSize(messagesRequest.amountOfMessages)
        );

        return ResponseEntity.ok(TableHelp.toJson(messages).toString());
    }

    private ResponseEntity<String> getMessagesBetween(MessagesRequest messagesRequest){
        LocalDateTime since = TableHelp.toDateTime(messagesRequest.sinceDateTimeMillis);
        LocalDateTime until = TableHelp.toDateTime(messagesRequest.untilDateTimeMillis);

        Page<Message> messages = messageRepository.findAllByChatIdAndXBetweenAndYBetweenAndTimeBetweenOrderByTimeDesc(
            messagesRequest.chatId,
            messagesRequest.xCoordLeftTop,
            messagesRequest.xCoordRightBottom,
            messagesRequest.yCoordLeftTop,
            messagesRequest.yCoordRightBottom,
            since,
            until,
            Pageable.ofSize(messagesRequest.amountOfMessages)
        );

        return ResponseEntity.ok(TableHelp.toJson(messages).toString());
    }

    @AllArgsConstructor
    @NoArgsConstructor
    static class MessagesRequest{
        long chatId;
        int xCoordLeftTop;
        int yCoordLeftTop;
        int xCoordRightBottom;
        int yCoordRightBottom;
        int amountOfMessages;
        long sinceDateTimeMillis;
        long untilDateTimeMillis;

    }
}






