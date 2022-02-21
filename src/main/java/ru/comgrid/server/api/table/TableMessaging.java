package ru.comgrid.server.api.table;

import lombok.AllArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import ru.comgrid.server.api.user.UserHelp;
import ru.comgrid.server.model.Message;
import ru.comgrid.server.repository.ChatParticipantsRepository;
import ru.comgrid.server.repository.MessageRepository;

import javax.websocket.server.ServerEndpoint;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Table messaging via SockJS(WebSocket)
 * @author MediaNik
 */
@Controller
public class TableMessaging{
    private final ChatParticipantsRepository participantsRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * @hidden
     */
    public TableMessaging(
        @Autowired ChatParticipantsRepository participantsRepository,
        @Autowired MessageRepository messageRepository,
        @Autowired SimpMessagingTemplate messagingTemplate
    ){
        this.participantsRepository = participantsRepository;
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/table")
    public void processMessage(
        @AuthenticationPrincipal OAuth2User user,
        @Payload Message chatMessage
    ){
        BigDecimal personId = UserHelp.extractId(user);
        boolean isInTable = participantsRepository.existsByChatAndPerson(chatMessage.getChatId(), personId);
//        boolean isInTable = true;
        if(!isInTable){
            messagingTemplate.convertAndSendToUser(
                (String) user.getAttributes().get("sub"),
                "/queue/table/exception",
                new ChatNotFoundException(404, "Could not find chat with such id")
            );
            return;
        }

        chatMessage.setId(null);
        chatMessage.setSenderId(personId);
        chatMessage.setTime(LocalDateTime.now(Clock.systemUTC()));
        Message message = messageRepository.save(chatMessage);

        messagingTemplate.convertAndSend("/connection/table/queue/" + chatMessage.getChatId().toString(), message);
    }

    @AllArgsConstructor
    private static class ChatNotFoundException{
        private int exceptionType;
        private String disclaimer;
    }
}
