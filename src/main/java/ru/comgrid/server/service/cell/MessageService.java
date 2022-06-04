package ru.comgrid.server.service.cell;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.comgrid.server.controller.message.MessagesRequest;
import ru.comgrid.server.exception.IllegalAccessException;
import ru.comgrid.server.exception.NotFoundException;
import ru.comgrid.server.exception.OutOfBoundsRequestException;
import ru.comgrid.server.model.Chat;
import ru.comgrid.server.model.Message;
import ru.comgrid.server.model.MessageId;
import ru.comgrid.server.model.Right;
import ru.comgrid.server.repository.ChatRepository;
import ru.comgrid.server.repository.MessageRepository;
import ru.comgrid.server.security.AppProperties;
import ru.comgrid.server.service.AccessService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static ru.comgrid.server.service.table.TableHelp.*;

@Service
public class MessageService{

    private final MessageRepository messageRepository;
    private final AccessService accessService;
    private final ChatRepository chatRepository;
    private final int chunkSize;

    public MessageService(
        @Autowired MessageRepository messageRepository,
        @Autowired AccessService accessService,
        @Autowired ChatRepository chatRepository,
        @Autowired AppProperties appProperties
    ){
        this.messageRepository = messageRepository;
        this.accessService = accessService;
        this.chatRepository = chatRepository;
        chunkSize = appProperties.getTable().getSearchChunkSize();
    }

    public List<Message> searchMessages(@NotNull String text, long chatId, long sinceTimeMillis, long untilTimeMillis, int chunkNumber, boolean exactMatch, @NotNull BigDecimal userId){
        if(checkTimeBorders(sinceTimeMillis, untilTimeMillis)){
            throw new OutOfBoundsRequestException("time.negative-or-future");
        }

        int offset = calculateOffset(chunkNumber);
        if(chatId != 0){ // particular chat
            return searchMessagesInChat(text, chatId, sinceTimeMillis, untilTimeMillis, exactMatch, userId, offset);
        }else{
            if(sinceTimeMillis == TIME_NOT_SPECIFIED && untilTimeMillis == TIME_NOT_SPECIFIED){
                if(exactMatch) return messageRepository.findExact(userId, text, offset, chunkSize);
                else return messageRepository.findSimilar(userId, text, offset, chunkSize);
            }else{
                LocalDateTime since = toDateTime(sinceTimeMillis);
                LocalDateTime until = toDateTime(untilTimeMillis);

                if(exactMatch)
                    return messageRepository.findExactForPeriod(userId, text, offset, chunkSize, since, until);
                else return messageRepository.findSimilarForPeriod(userId, text, offset, chunkSize, since, until);
            }
        }
    }

    private int calculateOffset(int chunkNumber){
        return chunkNumber*chunkSize;
    }

    private List<Message> searchMessagesInChat(@NotNull String text, long chatId, long sinceTimeMillis, long untilTimeMillis, boolean exactMatch, @NotNull BigDecimal userId, int offset){
        if(!accessService.hasAccessTo(userId, chatId, Right.Read)){
            throw new IllegalAccessException("chat.read_messages");
        }

        if(sinceTimeMillis == TIME_NOT_SPECIFIED && untilTimeMillis == TIME_NOT_SPECIFIED){
            if(exactMatch) return messageRepository.findExactInChat(chatId, text, offset, chunkSize);
            else return messageRepository.findSimilarInChat(chatId, text, offset, chunkSize);
        }else{
            LocalDateTime since = toDateTime(sinceTimeMillis);
            LocalDateTime until = toDateTime(untilTimeMillis);
            if(exactMatch)
                return messageRepository.findExactInChatForPeriod(chatId, text, offset, chunkSize, since, until);
            else return messageRepository.findSimilarInChatForPeriod(chatId, text, offset, chunkSize, since, until);
        }
    }

    public Message getSingleMessage(long chatId, int x, int y, @NotNull BigDecimal userId){
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

    public List<Message> getMessagesOfChat(@NotNull MessagesRequest messagesRequest, @NotNull BigDecimal userId){
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
