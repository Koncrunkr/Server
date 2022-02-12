package ru.comgrid.server.api.table;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.springframework.data.domain.Page;
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

    public static boolean checkBorders(Chat chat, TableService.MessagesRequest messagesRequest){
        return messagesRequest.xCoordLeftTop < 0 ||
            messagesRequest.xCoordLeftTop > messagesRequest.xCoordRightBottom ||
            messagesRequest.xCoordRightBottom >= chat.getWidth() ||
            messagesRequest.yCoordLeftTop < 0 ||
            messagesRequest.yCoordLeftTop > messagesRequest.yCoordRightBottom ||
            messagesRequest.yCoordRightBottom >= chat.getHeight();
    }

    public static boolean checkTimeBorders(TableService.MessagesRequest messagesRequest){
        return messagesRequest.sinceDateTimeMillis < 0 ||
            messagesRequest.sinceDateTimeMillis > System.currentTimeMillis() ||
            messagesRequest.untilDateTimeMillis <= 0 ||
            messagesRequest.untilDateTimeMillis > System.currentTimeMillis();
    }
}
