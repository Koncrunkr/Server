package ru.comgrid.server.api.message;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import ru.comgrid.server.exception.IllegalAccessException;
import ru.comgrid.server.exception.OutOfBoundsRequestException;
import ru.comgrid.server.exception.TooBigRequestException;
import ru.comgrid.server.exception.WrongRequestException;
import ru.comgrid.server.model.CellUnion;
import ru.comgrid.server.model.Chat;
import ru.comgrid.server.model.Message;
import ru.comgrid.server.model.Right;
import ru.comgrid.server.repository.ChatRepository;
import ru.comgrid.server.repository.MessageRepository;
import ru.comgrid.server.repository.CellUnionRepository;

import java.time.LocalDateTime;
import java.util.List;

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
    @Operation(summary = "get messages of chat", description = "Get messages of table in given square with specified topLeft and bottomRight point.")
    @PostMapping("/list")
    public ResponseEntity<List<Message>> getMessages(
        @AuthenticationPrincipal OAuth2User user,
        @RequestBody MessagesRequest messagesRequest
    ){
        var userId = UserHelp.extractId(user);

        if(!accessService.hasAccessTo(userId, messagesRequest.chatId, Right.Read))
            throw new IllegalAccessException("chat.read_messages");

        if(messagesRequest.amountOfMessages > maxMessagesSize)
            throw new TooBigRequestException("chat_messages", maxMessagesSize);

        if(messagesRequest.amountOfMessages < 0)
            throw new WrongRequestException("messages.negative");

        if(messagesRequest.amountOfMessages == 0)
            messagesRequest.amountOfMessages = defaultPageSize;

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        Chat chat = chatRepository.findById(messagesRequest.chatId).get();

        if(TableHelp.checkBorders(chat, messagesRequest)){
            throw new OutOfBoundsRequestException();
        }

        if(TableHelp.checkTimeBorders(messagesRequest)){
            throw new OutOfBoundsRequestException("time.negative-or-future");
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

    @ApiResponse(responseCode = "403", description = "Forbidden access. Error code: chat.read_messages", content = @Content())
    @ApiResponse(responseCode = "400", description = "Bad request. Error code: chat.out_of_borders", content = @Content())
    @ApiResponse(responseCode = "200")
    @Operation(summary = "Get cell unions", description = """
        Suppose you want to get union cells inside some square:
        ![Square](https://sun9-75.userapi.com/impg/o3MOVJFYabFR1upRd_S9x6msrbT7pUGs6pHp3g/DXhWOP-6kx0.jpg?size=1338x694&quality=96&sign=53015cac24b6463a5d97329a005ca4f6&type=album)
                
        This request allows you to get all the checked cell unions
        AND question marked ones(but not crossed out)
        """)
    @GetMapping("/unions")
    public ResponseEntity<List<CellUnion>> cellUnions(
        @AuthenticationPrincipal OAuth2User user,
        @RequestParam long chatId,
        @RequestParam(required = false, defaultValue = "0") int xcoordLeftTop,
        @RequestParam(required = false, defaultValue = "0") int ycoordLeftTop,
        @RequestParam(required = false, defaultValue = "0") int xcoordRightBottom,
        @RequestParam(required = false, defaultValue = "0") int ycoordRightBottom
    ){
        var userId = UserHelp.extractId(user);

        if(!accessService.hasAccessTo(userId, chatId, Right.Read)){
            throw new IllegalAccessException("chat.read_messages");
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        Chat chat = chatRepository.findById(chatId).get();

        if(TableHelp.checkBorders(chat, new MessageUnionRequest(chatId, xcoordLeftTop, ycoordLeftTop, xcoordRightBottom, ycoordRightBottom))){
            throw new OutOfBoundsRequestException("chat.out_of_bounds");
        }

        return ResponseEntity.ok(
            cellUnionRepository.findAllByChat(
                chatId,
                xcoordLeftTop,
                ycoordLeftTop,
                xcoordRightBottom,
                ycoordRightBottom,
                Pageable.ofSize(defaultPageSize)
            ).getContent());
    }


    public ResponseEntity<List<Message>> getLastMessages(@NotNull MessagesRequest messagesRequest){
        Page<Message> messages = messageRepository.findAllByChatIdAndXBetweenAndYBetweenOrderByTimeDesc(
            messagesRequest.chatId, messagesRequest.xcoordLeftTop, messagesRequest.xcoordRightBottom,
            messagesRequest.ycoordLeftTop, messagesRequest.ycoordRightBottom,
            Pageable.ofSize(messagesRequest.amountOfMessages)
        );

        return ResponseEntity.ok(messages.getContent());
    }
    public ResponseEntity<List<Message>> getMessagesUntil(@NotNull MessagesRequest messagesRequest){
        LocalDateTime until = TableHelp.toDateTime(messagesRequest.untilDateTimeMillis);

        Page<Message> messages = messageRepository.findAllByChatIdAndXBetweenAndYBetweenAndTimeBeforeOrderByTimeDesc(
            messagesRequest.chatId,
            messagesRequest.xcoordLeftTop,
            messagesRequest.xcoordRightBottom,
            messagesRequest.ycoordLeftTop,
            messagesRequest.ycoordRightBottom,
            until,
            Pageable.ofSize(messagesRequest.amountOfMessages)
        );

        return ResponseEntity.ok(messages.getContent());
    }
    public ResponseEntity<List<Message>> getMessagesSince(@NotNull MessagesRequest messagesRequest){
        LocalDateTime since = TableHelp.toDateTime(messagesRequest.sinceDateTimeMillis);

        Page<Message> messages = messageRepository.findAllByChatIdAndXBetweenAndYBetweenAndTimeAfterOrderByTimeDesc(
            messagesRequest.chatId,
            messagesRequest.xcoordLeftTop,
            messagesRequest.xcoordRightBottom,
            messagesRequest.ycoordLeftTop,
            messagesRequest.ycoordRightBottom,
            since,
            Pageable.ofSize(messagesRequest.amountOfMessages)
        );

        return ResponseEntity.ok(messages.getContent());
    }
    public ResponseEntity<List<Message>> getMessagesBetween(@NotNull MessagesRequest messagesRequest){
        LocalDateTime since = TableHelp.toDateTime(messagesRequest.sinceDateTimeMillis);
        LocalDateTime until = TableHelp.toDateTime(messagesRequest.untilDateTimeMillis);

        Page<Message> messages = messageRepository.findAllByChatIdAndXBetweenAndYBetweenAndTimeBetweenOrderByTimeDesc(
            messagesRequest.chatId,
            messagesRequest.xcoordLeftTop,
            messagesRequest.xcoordRightBottom,
            messagesRequest.ycoordLeftTop,
            messagesRequest.ycoordRightBottom,
            since,
            until,
            Pageable.ofSize(messagesRequest.amountOfMessages)
        );

        return ResponseEntity.ok(messages.getContent());
    }
}
