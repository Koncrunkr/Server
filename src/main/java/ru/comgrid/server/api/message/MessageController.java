package ru.comgrid.server.api.message;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.comgrid.server.api.user.AccessService;
import ru.comgrid.server.api.user.UserHelp;
import ru.comgrid.server.exception.IllegalAccessException;
import ru.comgrid.server.exception.NotFoundException;
import ru.comgrid.server.exception.OutOfBoundsRequestException;
import ru.comgrid.server.model.*;
import ru.comgrid.server.repository.CellUnionRepository;
import ru.comgrid.server.repository.ChatRepository;
import ru.comgrid.server.repository.MessageRepository;
import ru.comgrid.server.security.AppProperties;
import ru.comgrid.server.security.annotation.CurrentUser;
import ru.comgrid.server.security.user.info.UserPrincipal;

import java.time.LocalDateTime;
import java.util.List;

import static ru.comgrid.server.api.table.TableHelp.*;

@RestController
@RequestMapping(value = "/message", produces = "application/json; charset=utf-8")
@SecurityRequirement(name = "bearerAuth")
public class MessageController{

    private final MessageRepository messageRepository;
    private final AccessService accessService;
    private final ChatRepository chatRepository;
    private final CellUnionRepository cellUnionRepository;
    private final int chunkSize;

    public MessageController(
        @Autowired MessageRepository messageRepository,
        @Autowired AccessService accessService,
        @Autowired ChatRepository chatRepository,
        @Autowired CellUnionRepository cellUnionRepository,
        @Autowired AppProperties appProperties
    ){
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.cellUnionRepository = cellUnionRepository;
        this.accessService = accessService;
        chunkSize = appProperties.getTable().getSearchChunkSize();
    }

    @Operation(
        summary = "search for messages in user's chatlist or in particular chat",
        description = """
            Get messages of either user's chatlist or single chat,
            that contain similar text to user's input
            """
    )
    @ApiResponse(responseCode = "403", description = "access.chat.read_messages, Cannot read messages in this chat")
    @ApiResponse(responseCode = "422", description = "time.negative-or-future, means you've entered negative time or time that has not yet happened")
    @GetMapping("/search")
    public List<Message> searchForMessages(
        @CurrentUser UserPrincipal user,
        @RequestParam String text,
        @RequestParam(required = false, defaultValue = "0") long chatId,
        @RequestParam(required = false, defaultValue = "0") long sinceTimeMillis,
        @RequestParam(required = false, defaultValue = "0") long untilTimeMillis,
        @RequestParam(required = false, defaultValue = "0") int chunkNumber,
        @RequestParam(required = false, defaultValue = "false") boolean exactMatch
    ){
        var userId = UserHelp.extractId(user);

        if(checkTimeBorders(sinceTimeMillis, untilTimeMillis)){
            throw new OutOfBoundsRequestException("time.negative-or-future");
        }

        int offset = calculateOffset(chunkNumber);
        if(chatId != 0){ // particular chat
            if(!accessService.hasAccessTo(userId, chatId, Right.Read)){
                throw new IllegalAccessException("chat.read_messages");
            }

            if(sinceTimeMillis == TIME_NOT_SPECIFIED && untilTimeMillis == TIME_NOT_SPECIFIED){
                if(exactMatch){
                    return messageRepository.findExactInChat(chatId, text, offset, chunkSize);
                }else{
                    return messageRepository.findSimilarInChat(chatId, text, offset, chunkSize);
                }
            }else{
                LocalDateTime since = toDateTime(sinceTimeMillis);
                LocalDateTime until = toDateTime(untilTimeMillis);
                if(exactMatch){
                    return messageRepository.findExactInChatForPeriod(chatId, text, offset, chunkSize, since, until);
                }else{
                    return messageRepository.findSimilarInChatForPeriod(chatId, text, offset, chunkSize, since, until);
                }
            }
        }else{
            if(sinceTimeMillis == TIME_NOT_SPECIFIED && untilTimeMillis == TIME_NOT_SPECIFIED){
                if(exactMatch){
                    return messageRepository.findExact(userId, text, offset, chunkSize);
                }else{
                    return messageRepository.findSimilar(userId, text, offset, chunkSize);
                }
            }else{
                LocalDateTime since = toDateTime(sinceTimeMillis);
                LocalDateTime until = toDateTime(untilTimeMillis);
                if(exactMatch){
                    return messageRepository.findExactForPeriod(userId, text, offset, chunkSize, since, until);
                }else{
                    return messageRepository.findSimilarForPeriod(userId, text, offset, chunkSize, since, until);
                }
            }
        }
    }

    private int calculateOffset(int chunkNumber){
        return chunkNumber*chunkSize;
    }

    @GetMapping("/")
    public Message getMessage(
        @CurrentUser UserPrincipal user,
        @RequestParam long chatId,
        @RequestParam int x,
        @RequestParam int y
    ){
        var userId = UserHelp.extractId(user);

        if(!accessService.hasAccessTo(userId, chatId, Right.Read)){
            throw new IllegalAccessException("chat.read_message");
        }

        Chat chat = chatRepository.findById(chatId).get();
        if(bordersWrong(chat, x, y)){
            throw new OutOfBoundsRequestException();
        }

        return messageRepository.findById(new MessageId(chatId, x, y))
            .orElseThrow(() -> new NotFoundException("message.not_found"));
    }

    @Operation(summary = "get messages of chat", description = "Get messages of table in given square with specified topLeft and bottomRight point.")
    @ApiResponse(responseCode = "403", description = "access.chat.read_messages, Cannot read messages in this chat")
    @ApiResponse(responseCode = "422", description = "out_of_bounds")
    @ApiResponse(responseCode = "422", description = "time.negative-or-future, means you've entered negative time or time that has not yet happened")
    @PostMapping("/list")
    public List<Message> getMessages(
        @CurrentUser UserPrincipal user,
        @RequestBody MessagesRequest messagesRequest
    ){
        var userId = UserHelp.extractId(user);

        if(!accessService.hasAccessTo(userId, messagesRequest.chatId, Right.Read))
            throw new IllegalAccessException("chat.read_messages");

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        Chat chat = chatRepository.findById(messagesRequest.chatId).get();

        if(bordersWrong(chat, messagesRequest)){
            throw new OutOfBoundsRequestException();
        }

        if(checkTimeBorders(messagesRequest.sinceTimeMillis, messagesRequest.untilTimeMillis)){
            throw new OutOfBoundsRequestException("time.negative-or-future");
        }

        if(messagesRequest.sinceTimeMillis == TIME_NOT_SPECIFIED &&
            messagesRequest.untilTimeMillis == TIME_NOT_SPECIFIED){
            // neither since nor until are specified, do fast getLastMessages
            return getLastMessages(messagesRequest);
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
        @CurrentUser UserPrincipal user,
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

        if(bordersWrong(chat, new MessageUnionRequest(chatId, xcoordLeftTop, ycoordLeftTop, xcoordRightBottom, ycoordRightBottom))){
            throw new OutOfBoundsRequestException("chat.out_of_bounds");
        }

        return ResponseEntity.ok(
            cellUnionRepository.findAllByChat(
                chatId,
                xcoordLeftTop,
                ycoordLeftTop,
                xcoordRightBottom,
                ycoordRightBottom
            ));
    }


    public List<Message> getLastMessages(@NotNull MessagesRequest messagesRequest){
        return messageRepository.findAllInChat(
            messagesRequest.chatId, messagesRequest.xcoordLeftTop, messagesRequest.xcoordRightBottom,
            messagesRequest.ycoordLeftTop, messagesRequest.ycoordRightBottom
        );
    }
    public List<Message> getMessagesBetween(@NotNull MessagesRequest messagesRequest){
        LocalDateTime since = toDateTime(messagesRequest.sinceTimeMillis);
        LocalDateTime until = toDateTime(messagesRequest.untilTimeMillis);

        return messageRepository.findAllInChatBetween(
            messagesRequest.chatId,
            messagesRequest.xcoordLeftTop,
            messagesRequest.xcoordRightBottom,
            messagesRequest.ycoordLeftTop,
            messagesRequest.ycoordRightBottom,
            since,
            until
        );
    }
}
