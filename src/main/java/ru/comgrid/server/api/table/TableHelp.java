package ru.comgrid.server.api.table;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.springframework.data.domain.Page;
import ru.comgrid.server.api.message.MessagesRequest;
import ru.comgrid.server.model.Chat;
import ru.comgrid.server.model.Message;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public enum TableHelp{;
    public static final Gson gson = new Gson();

    public static LocalDateTime toDateTime(long millis){
        return LocalDateTime.ofEpochSecond(
            millis / 1000,
            (int) (millis % 1000 * 1000),
            ZoneOffset.UTC
        );
    }

    public static JsonArray toJson(Page<Message> messagePages){
        JsonArray messages = new JsonArray(messagePages.getNumberOfElements());
        for (Message messagePage : messagePages){
            messages.add(gson.toJsonTree(messagePage));
        }
        return messages;
    }

    /**
     * This method WILL change the {@code messagesRequest}
     * if it contains no borders(they are set to 0)
     * @param chat corresponding chat
     * @param messagesRequest request info containing borders
     * @return true if borders are out of bounds, false if everything's ok
     */
    public static boolean checkBorders(Chat chat, MessagesRequest messagesRequest){
        if(messagesRequest.xCoordLeftTop == 0 &&
            messagesRequest.yCoordLeftTop == 0 &&
            messagesRequest.xCoordRightBottom == 0 &&
            messagesRequest.yCoordRightBottom == 0
        ){
            messagesRequest.xCoordRightBottom = chat.getWidth() - 1;
            messagesRequest.yCoordRightBottom = chat.getHeight() - 1;
            return true;
        }
        return messagesRequest.xCoordLeftTop < 0 ||
            messagesRequest.xCoordLeftTop > messagesRequest.xCoordRightBottom ||
            messagesRequest.xCoordRightBottom >= chat.getWidth() ||
            messagesRequest.yCoordLeftTop < 0 ||
            messagesRequest.yCoordLeftTop > messagesRequest.yCoordRightBottom ||
            messagesRequest.yCoordRightBottom >= chat.getHeight();
    }

    public static boolean checkTimeBorders(MessagesRequest messagesRequest){
        return messagesRequest.sinceDateTimeMillis < 0 ||
            messagesRequest.sinceDateTimeMillis > System.currentTimeMillis() ||
            messagesRequest.untilDateTimeMillis < 0 ||
            messagesRequest.untilDateTimeMillis > System.currentTimeMillis();
    }
}
