package ru.comgrid.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.comgrid.server.model.Message;
import ru.comgrid.server.model.MessageId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, MessageId>{
    Optional<Message> findMessageByChatIdAndXAndY(@Param("chatId") Long chatId, @Param("x") Integer x, @Param("y") Integer y);

    @Query("""
        select m from Message m
        where m.chatId = :chatId and m.x between :startX and :endX and m.y between :startY and :endY
        order by m.edited DESC""")
    List<Message> findAllInChat(
        @Param("chatId") Long chatId,
        @Param("startX") Integer startX,
        @Param("endX") Integer endX,
        @Param("startY") Integer startY,
        @Param("endY") Integer endY
    );

    @Query("""
        select m from Message m
        where m.chatId = :chatId and m.x between :startX and :endX and m.y between :startY and :endY and m.edited between :startTime and :endTime
        order by m.edited DESC""")
    List<Message> findAllInChatBetween(
        @Param("chatId") Long chatId,
        @Param("startX") Integer startX,
        @Param("endX") Integer endX,
        @Param("startY") Integer startY,
        @Param("endY") Integer endY,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
}
