package ru.comgrid.server.service.table;

import ru.comgrid.server.controller.message.MessageUnionRequest;
import ru.comgrid.server.model.CellUnion;
import ru.comgrid.server.model.Chat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public enum TableHelp{;
    public static final long TIME_NOT_SPECIFIED = 0;

    public static LocalDateTime toDateTime(long millis){
        return LocalDateTime.ofEpochSecond(
            millis / 1000,
            (int) (millis % 1000 * 1000),
            ZoneOffset.UTC
        );
    }

    /**
     * This method WILL change the {@code messagesRequest}
     * if it contains no borders(they are set to 0)
     * @param chat corresponding chat
     * @param messagesRequest request info containing borders
     * @return true if borders are out of bounds, false if everything's ok
     */
    public static boolean bordersWrong(Chat chat, MessageUnionRequest messagesRequest){
        if(messagesRequest.xcoordLeftTop == 0 &&
            messagesRequest.ycoordLeftTop == 0 &&
            messagesRequest.xcoordRightBottom == 0 &&
            messagesRequest.ycoordRightBottom == 0
        ){
            messagesRequest.xcoordRightBottom = chat.getWidth() - 1;
            messagesRequest.ycoordRightBottom = chat.getHeight() - 1;
            return false;
        }
        return messagesRequest.xcoordLeftTop < 0 ||
            messagesRequest.xcoordLeftTop > messagesRequest.xcoordRightBottom ||
            messagesRequest.xcoordRightBottom >= chat.getWidth() ||
            messagesRequest.ycoordLeftTop < 0 ||
            messagesRequest.ycoordLeftTop > messagesRequest.ycoordRightBottom ||
            messagesRequest.ycoordRightBottom >= chat.getHeight();
    }

    public static boolean bordersWrong(Chat chat, int x, int y){
        return x < 0 || y < 0 || x >= chat.getWidth() || y > chat.getHeight();
    }

    public static boolean checkTimeBorders(long from, long to){
        return (from != TIME_NOT_SPECIFIED && (from < 0 ||
            from > System.currentTimeMillis())) ||
            (to != TIME_NOT_SPECIFIED && (to < 0 ||
                to > System.currentTimeMillis()));
    }

    public static boolean isInside(CellUnion inside, CellUnion outbound){
        return outbound.getXcoordLeftTop() <= inside.getXcoordLeftTop() &&
            outbound.getYcoordLeftTop() <= inside.getYcoordLeftTop() &&
            outbound.getXcoordRightBottom() >= inside.getXcoordRightBottom() &&
            outbound.getYcoordLeftTop() >= inside.getYcoordRightBottom();
    }
}
