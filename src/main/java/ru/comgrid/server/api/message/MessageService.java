package ru.comgrid.server.api.message;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import ru.comgrid.server.api.table.TableHelp;
import ru.comgrid.server.api.user.AccessService;
import ru.comgrid.server.api.user.UserHelp;
import ru.comgrid.server.model.Chat;
import ru.comgrid.server.model.Message;
import ru.comgrid.server.model.Right;
import ru.comgrid.server.repository.ChatRepository;
import ru.comgrid.server.repository.MessageRepository;
import ru.comgrid.server.repository.CellUnionRepository;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/message", produces = "application/json; charset=utf-8")
public class MessageService{

    private final MessageRepository messageRepository;
    private final AccessService accessService;
    private final ChatRepository chatRepository;
    private final CellUnionRepository cellUnionRepository;
    private final int maxMessagesSize;
    private final int defaultPageSize;

    public MessageService(
        @Autowired MessageRepository messageRepository,
        @Autowired AccessService accessService,
        @Autowired ChatRepository chatRepository,
        @Autowired CellUnionRepository cellUnionRepository,
        @Value("${ru.comgrid.chat.messages.max}") int maxMessagesSize,
        @Value("${ru.comgrid.chat.participants.default-page-size}") int defaultPageSize
    ){
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.cellUnionRepository = cellUnionRepository;
        this.maxMessagesSize = maxMessagesSize;
        this.accessService = accessService;
        this.defaultPageSize = defaultPageSize;
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
    @PostMapping("/list")
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
            return getLastMessages(messagesRequest);
        }else if(messagesRequest.sinceDateTimeMillis == 0){
            // only until is specified
            return getMessagesUntil(messagesRequest);
        }else if(messagesRequest.untilDateTimeMillis == 0){
            // only since is specified
            return getMessagesSince(messagesRequest);
        }else{
            // specified both since and until
            return getMessagesBetween(messagesRequest);
        }
    }


    @GetMapping("/unions")
    public ResponseEntity<String> cellUnions(
        @AuthenticationPrincipal OAuth2User user,
        @RequestParam long chatId,
        @RequestParam(required = false, defaultValue = "0") int xcoordLeftTop,
        @RequestParam(required = false, defaultValue = "0") int ycoordLeftTop,
        @RequestParam(required = false, defaultValue = "0") int xcoordRightBottom,
        @RequestParam(required = false, defaultValue = "0") int ycoordRightBottom
    ){
        var userId = UserHelp.extractId(user);

        if(!accessService.hasAccessTo(userId, chatId, Right.Read)){
            return ResponseEntity.status(403).body("{\"timestamp\": \"" + LocalDateTime.now() + "\", \"status\": 403, \"reason\": \"Sorry, you don't have access to read messages in this chat\"}");
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        Chat chat = chatRepository.findById(chatId).get();

        if(TableHelp.checkBorders(chat, new MessageUnionRequest(chatId, xcoordLeftTop, ycoordLeftTop, xcoordRightBottom, ycoordRightBottom))){
            return ResponseEntity.badRequest().body("{\"timestamp\": \"" + LocalDateTime.now() + "\", \"status\": 400, \"reason\": \"You are out of borders\"}");
        }

        return ResponseEntity.ok(
            TableHelp.toJson(cellUnionRepository.findAllByChat(
                chatId,
                xcoordLeftTop,
                ycoordLeftTop,
                xcoordRightBottom,
                ycoordRightBottom,
                Pageable.ofSize(defaultPageSize)
            )).toString());
    }


    public ResponseEntity<String> getLastMessages(@NotNull MessagesRequest messagesRequest){
        Page<Message> messages = messageRepository.findAllByChatIdAndXBetweenAndYBetweenOrderByTimeDesc(
            messagesRequest.chatId, messagesRequest.xCoordLeftTop, messagesRequest.xCoordRightBottom,
            messagesRequest.yCoordLeftTop, messagesRequest.yCoordRightBottom,
            Pageable.ofSize(messagesRequest.amountOfMessages)
        );

        String body = TableHelp.toJson(messages).toString();
        System.out.println(body);
        return ResponseEntity.ok(body);
    }
    public ResponseEntity<String> getMessagesUntil(@NotNull MessagesRequest messagesRequest){
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
    public ResponseEntity<String> getMessagesSince(@NotNull MessagesRequest messagesRequest){
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
    public ResponseEntity<String> getMessagesBetween(@NotNull MessagesRequest messagesRequest){
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
}
