package ru.comgrid.server.api.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import ru.comgrid.server.api.WebsocketDestination;
import ru.comgrid.server.api.user.AccessService;
import ru.comgrid.server.api.user.UserHelp;
import ru.comgrid.server.exception.RequestException;
import ru.comgrid.server.model.CellUnion;
import ru.comgrid.server.model.Message;
import ru.comgrid.server.model.MessageId;
import ru.comgrid.server.repository.CellUnionRepository;
import ru.comgrid.server.repository.MessageRepository;
import ru.comgrid.server.security.annotation.CurrentUser;
import ru.comgrid.server.security.user.info.UserPrincipal;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Table messaging via SockJS(WebSocket)
 * @author MediaNik
 */
@Controller
public class WebsocketMessaging{
    private final CellUnionRepository cellUnionRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final AccessService accessService;

    /**
     * @hidden
     */
    public WebsocketMessaging(
        @Autowired CellUnionRepository cellUnionRepository,
        @Autowired MessageRepository messageRepository,
        @Autowired SimpMessagingTemplate messagingTemplate,
        @Autowired AccessService accessService
    ){
        this.cellUnionRepository = cellUnionRepository;
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
        this.accessService = accessService;
    }

//    @Transactional
//    @MessageMapping("/table_message/edit_or_send")
//    public void processNewOrEditMessage(
//        @CurrentUser UserPrincipal user,
//        @Payload Message chatMessage
//    ){
//        if(chatMessage.getId() != null){
//            processEditMessage(user, chatMessage);
//            return;
//        }
//
//        Optional<Message> message = messageRepository.findMessageByChatIdAndXAndY(chatMessage.getChatId(), chatMessage.getX(), chatMessage.getY());
//        if(message.isPresent()){
//            chatMessage.setId(message.get().getId());
//            processEditMessage(user, chatMessage);
//        }else{
//            processNewMessage(user, chatMessage);
//        }
//    }

    private void sendException(BigDecimal personId, RequestException requestException){
        messagingTemplate.convertAndSend(WebsocketDestination.USER.destination(personId), requestException);
    }

    @Transactional
    @MessageMapping("/table_message/edit_or_send")
    public void processNewMessage(
        @CurrentUser UserPrincipal user,
        @Payload Message chatMessage
    ){
        BigDecimal personId = UserHelp.extractId(user);
        chatMessage.setSenderId(personId);
        if(!accessService.hasAccessToSendMessage(personId, chatMessage)){
            return;
        }

        Optional<Message> oldMessage = messageRepository.findById(new MessageId(chatMessage.getChatId(), chatMessage.getX(), chatMessage.getY()));
        Message message;
        if(oldMessage.isEmpty()){
            chatMessage.setCreated(LocalDateTime.now(Clock.systemUTC()));
            chatMessage.setEdited(LocalDateTime.now(Clock.systemUTC()));
            message = messageRepository.save(chatMessage);
        }else{
            oldMessage.get().setText(chatMessage.getText());
            oldMessage.get().setEdited(LocalDateTime.now());
            message = messageRepository.save(oldMessage.get());
        }

        messagingTemplate.convertAndSend(WebsocketDestination.TABLE_MESSAGE.destination(chatMessage.getChatId()), message);
    }

//    @Transactional
//    @MessageMapping("/table_message/edit")
//    public void processEditMessage(
//        @CurrentUser UserPrincipal user,
//        @Payload Message chatMessage
//    ){
//        BigDecimal personId = UserHelp.extractId(user);
//        chatMessage.setSenderId(personId);
//
//        if(!accessService.hasAccessToEditMessage(personId, chatMessage)){
//            return;
//        }
//
//        Message oldMessage = messageRepository.getById(chatMessage.getId());
//        oldMessage.setEdited(LocalDateTime.now(Clock.systemUTC()));
//        oldMessage.setText(chatMessage.getText());
//        Message message = messageRepository.save(oldMessage);
//
//        messagingTemplate.convertAndSend(WebsocketDestination.TABLE_MESSAGE.destination(chatMessage.getChatId()), message);
//    }

    @Transactional
    @MessageMapping("/table_cell_union")
    public void processNewCellsUnion(
        @CurrentUser UserPrincipal user,
        @Payload CellUnion newCellUnion
    ){
        BigDecimal personId = UserHelp.extractId(user);
        if(!accessService.hasAccessToCreateCellUnion(personId, newCellUnion)){
            return;
        }
        newCellUnion.setCreatorId(personId);

        CellUnion cellUnion = cellUnionRepository.save(newCellUnion);
        messagingTemplate.convertAndSend(WebsocketDestination.TABLE_UNION.destination(newCellUnion.getChatId()), cellUnion);
    }

    @Transactional
    @MessageMapping("/table_cell_union/edit")
    public void processEditCellsUnion(
        @CurrentUser UserPrincipal user,
        @Payload CellUnion existingCellUnion
    ){
        BigDecimal personId = UserHelp.extractId(user);
        if(!accessService.hasAccessToEditCellUnion(personId, existingCellUnion)){
            return;
        }
        existingCellUnion.setCreatorId(personId);

        CellUnion cellUnion = cellUnionRepository.save(existingCellUnion);
        messagingTemplate.convertAndSend(WebsocketDestination.TABLE_UNION.destination(cellUnion.getChatId()), cellUnion);
    }

}
