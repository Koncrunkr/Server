package ru.comgrid.server.controller.message;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.comgrid.server.model.Message;
import ru.comgrid.server.security.annotation.CurrentUser;
import ru.comgrid.server.security.user.info.UserPrincipal;
import ru.comgrid.server.service.cell.MessageService;
import ru.comgrid.server.service.user.UserHelp;

import java.util.List;

@RestController
@RequestMapping(value = "/message", produces = "application/json; charset=utf-8")
@SecurityRequirement(name = "bearerAuth")
public class MessageController{

    private final MessageService messageService;

    public MessageController(@Autowired MessageService messageService){
        this.messageService = messageService;
    }

    @Operation(
        summary = "search for messages in user's chatlist or in particular chat",
        description = """
            Get messages of either user's chatlist or single chat,
            that contain similar text to user's input
            """
    )
    @ApiResponse(responseCode = "403", description = "access.chat.read_messages. Cannot read messages in this chat")
    @ApiResponse(responseCode = "422", description = "time.negative-or-future. You've entered negative time or time that has not yet happened")
    @GetMapping("/search")
    public List<Message> searchMessages(
        @CurrentUser UserPrincipal user,
        @RequestParam String text,
        @RequestParam(required = false, defaultValue = "0") long chatId,
        @RequestParam(required = false, defaultValue = "0") long sinceTimeMillis,
        @RequestParam(required = false, defaultValue = "0") long untilTimeMillis,
        @RequestParam(required = false, defaultValue = "0") int chunkNumber,
        @RequestParam(required = false, defaultValue = "false") boolean exactMatch
    ){
        var userId = UserHelp.extractId(user);

        return messageService.searchMessages(text, chatId, sinceTimeMillis, untilTimeMillis, chunkNumber, exactMatch, userId);
    }

    @ApiResponse(responseCode = "422", description = "out_of_bounds. Cannot get messages, because x/y is violating constraints on width, height")
    @ApiResponse(responseCode = "404", description = "message.not_found. Message does not exist")
    @ApiResponse(responseCode = "403", description = "access.message.not_found. Person doesn't have rights to read messages in this chat")
    @GetMapping("/")
    public Message getSingleMessage(
        @CurrentUser UserPrincipal user,
        @RequestParam long chatId,
        @RequestParam int x,
        @RequestParam int y
    ){
        var userId = UserHelp.extractId(user);

        return messageService.getSingleMessage(chatId, x, y, userId);
    }

    @Operation(summary = "get messages of chat", description = "Get messages of table in given square with specified topLeft and bottomRight point.")
    @ApiResponse(responseCode = "403", description = "access.chat.read_messages. Cannot read messages in this chat")
    @ApiResponse(responseCode = "422", description = "out_of_bounds. Cannot get messages, because x/y is violating constraints on width, height")
    @ApiResponse(responseCode = "422", description = "time.negative-or-future. You've entered negative time or time that has not yet happened")
    @PostMapping("/list")
    public List<Message> getMessages(
        @CurrentUser UserPrincipal user,
        @RequestBody MessagesRequest messagesRequest
    ){
        var userId = UserHelp.extractId(user);

        return messageService.getMessagesOfChat(messagesRequest, userId);
    }
}
