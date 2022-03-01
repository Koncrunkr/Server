package ru.comgrid.server.api.table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import ru.comgrid.server.api.user.AccessService;
import ru.comgrid.server.api.user.UserHelp;
import ru.comgrid.server.exception.IllegalAccessException;
import ru.comgrid.server.model.CellUnion;
import ru.comgrid.server.model.Message;
import ru.comgrid.server.repository.CellUnionRepository;
import ru.comgrid.server.repository.MessageRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Table messaging via SockJS(WebSocket)
 * @author MediaNik
 */
@Controller
public class TableMessaging{
    private final CellUnionRepository cellUnionRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final AccessService accessService;
    private final int defaultPageSize;

    /**
     * @hidden
     */
    public TableMessaging(
        @Autowired CellUnionRepository cellUnionRepository,
        @Autowired MessageRepository messageRepository,
        @Autowired SimpMessagingTemplate messagingTemplate,
        @Autowired AccessService accessService,
        @Value("${ru.comgrid.chat.default-page-size}") int defaultPageSize
    ){
        this.cellUnionRepository = cellUnionRepository;
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
        this.accessService = accessService;
        this.defaultPageSize = defaultPageSize;
    }

    @MessageMapping("/table")
    public void processMessage(
        @AuthenticationPrincipal OAuth2User user,
        @Payload Message chatMessage
    ){
        BigDecimal personId = UserHelp.extractId(user);
        Long chatId = chatMessage.getChatId();
        Optional<Message> existingMessage = messageRepository.findMessageByChatIdAndXAndY(chatId, chatMessage.getX(), chatMessage.getY());
        if(!accessService.hasAccessToSendOrEditMessage(personId, chatMessage, existingMessage)){
            messagingTemplate.convertAndSendToUser(
                (String) user.getAttributes().get("sub"),
                "/queue/table/exception",
                new IllegalAccessException("to send messages in this chat")
            );
            return;
        }

        chatMessage.setId(existingMessage.map(Message::getId).orElse(null));
        chatMessage.setSenderId(personId);
        chatMessage.setTime(LocalDateTime.now(Clock.systemUTC()));
        Message message = messageRepository.save(chatMessage);

        messagingTemplate.convertAndSend("/connection/table/queue/" + chatId, message);
    }

    @MessageMapping("/cell_union")
    public void processCellsUnion(
        @AuthenticationPrincipal OAuth2User user,
        @Payload CellUnion cellUnion
    ){
        BigDecimal personId = UserHelp.extractId(user);
        Long chatId = cellUnion.getChatId();
        if(cellUnion.getId() != null){
            Optional<CellUnion> existingCellUnion = cellUnionRepository.findById(cellUnion.getId());
            accessService.hasAccessToCreateOrEditCellUnion(personId, cellUnion, existingCellUnion);
            if(existingCellUnion.isPresent()){
                if(existingCellUnion.get().getCreatorId().compareTo(personId) != 0){

                }
            }
        }

        Page<CellUnion> cellUnionsIntersected = cellUnionRepository.findAllByChat(
            chatId, cellUnion.getXcoordLeftTop(), cellUnion.getYcoordLeftTop(),
            cellUnion.getXcoordRightBottom(), cellUnion.getYcoordRightBottom(),
            Pageable.ofSize(defaultPageSize)
        );

        for(var intersected : cellUnionsIntersected){

        }
    }
}
