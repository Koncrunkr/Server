package ru.comgrid.server.api.table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import ru.comgrid.server.api.user.AccessService;
import ru.comgrid.server.api.user.UserHelp;
import ru.comgrid.server.model.CellUnion;
import ru.comgrid.server.model.Message;
import ru.comgrid.server.repository.CellUnionRepository;
import ru.comgrid.server.repository.ChatParticipantsRepository;
import ru.comgrid.server.repository.MessageRepository;
import ru.comgrid.server.security.destination.IndividualDestinationInterceptor;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

import static ru.comgrid.server.api.message.MessageHelp.tableDestination;

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

    /**
     * @hidden
     */
    public TableMessaging(
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

    @MessageMapping("/table_message")
    public void processNewMessage(
        @AuthenticationPrincipal OAuth2User user,
        @Payload Message chatMessage
    ){
        BigDecimal personId = UserHelp.extractId(user);
        chatMessage.setSenderId(personId);
        if(!accessService.hasAccessToSendMessage(personId, chatMessage)){
            return;
        }

        chatMessage.setTime(LocalDateTime.now(Clock.systemUTC()));
        Message message = messageRepository.save(chatMessage);

        messagingTemplate.convertAndSend(tableDestination(chatMessage.getChatId()), message);
    }

    @MessageMapping("/table_message/edit")
    public void processEditMessage(
        @AuthenticationPrincipal OAuth2User user,
        @Payload Message chatMessage
    ){
        BigDecimal personId = UserHelp.extractId(user);

        if(!accessService.hasAccessToEditMessage(personId, chatMessage)){
            return;
        }

        chatMessage.setSenderId(personId);
        chatMessage.setTime(LocalDateTime.now(Clock.systemUTC()));
        Message message = messageRepository.save(chatMessage);

        messagingTemplate.convertAndSend(tableDestination(chatMessage.getChatId()), message);
    }


    @Component
    public static class TableMessageDestinationInterceptor implements IndividualDestinationInterceptor{
        private final ChatParticipantsRepository participantsRepository;
        public TableMessageDestinationInterceptor(@Autowired ChatParticipantsRepository participantsRepository){this.participantsRepository = participantsRepository;}
        @Override
        public String destination(){
            return "table_message";
        }
        @Override
        public boolean hasAccess(BigDecimal userId, String destinationId){
            return participantsRepository.existsByChatAndPerson(Long.valueOf(destinationId), userId);
        }
    }

    @Transactional
    @MessageMapping("/table_cell_union")
    public void processNewCellsUnion(
        @AuthenticationPrincipal OAuth2User user,
        @Payload CellUnion newCellUnion
    ){
        BigDecimal personId = UserHelp.extractId(user);
        if(!accessService.hasAccessToCreateCellUnion(personId, newCellUnion)){
            return;
        }
        newCellUnion.setCreatorId(personId);

        CellUnion cellUnion = cellUnionRepository.save(newCellUnion);
        messagingTemplate.convertAndSend(tableDestination(cellUnion.getChatId()), cellUnion);
    }

    @Transactional
    @MessageMapping("/table_cell_union/edit")
    public void processEditCellsUnion(
        @AuthenticationPrincipal OAuth2User user,
        @Payload CellUnion newCellUnion
    ){
        BigDecimal personId = UserHelp.extractId(user);
        if(!accessService.hasAccessToEditCellUnion(personId, newCellUnion)){
            return;
        }
        newCellUnion.setCreatorId(personId);

        CellUnion cellUnion = cellUnionRepository.save(newCellUnion);
        messagingTemplate.convertAndSend(tableDestination(cellUnion.getChatId()), cellUnion);
    }
}
