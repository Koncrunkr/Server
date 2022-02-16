package ru.comgrid.server.api.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.comgrid.server.api.table.TableHelp;
import ru.comgrid.server.model.Message;
import ru.comgrid.server.repository.MessageRepository;

import java.time.LocalDateTime;

@Service
public class MessageService{

    private final MessageRepository messageRepository;

    public MessageService(@Autowired MessageRepository messageRepository){this.messageRepository = messageRepository;}

    public ResponseEntity<String> getLastMessages(MessagesRequest messagesRequest){
        Page<Message> messages = messageRepository.findAllByChatIdAndXBetweenAndYBetweenOrderByTimeDesc(
            messagesRequest.chatId, messagesRequest.xCoordLeftTop, messagesRequest.xCoordRightBottom,
            messagesRequest.yCoordLeftTop, messagesRequest.yCoordRightBottom,
            Pageable.ofSize(messagesRequest.amountOfMessages)
        );

        String body = TableHelp.toJson(messages).toString();
        System.out.println(body);
        return ResponseEntity.ok(body);
    }

    public ResponseEntity<String> getMessagesUntil(MessagesRequest messagesRequest){
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

    public ResponseEntity<String> getMessagesSince(MessagesRequest messagesRequest){
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

    public ResponseEntity<String> getMessagesBetween(MessagesRequest messagesRequest){
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
