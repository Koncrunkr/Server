package ru.comgrid.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.comgrid.server.model.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long>{
    Optional<Message> findMessageByChatIdAndXAndY(@Param("chatId") Long chatId, @Param("x") Integer x, @Param("y") Integer y);

    List<Message> findAllByChatIdAndXBetweenAndYBetweenOrderByEditedDesc(
        @Param("chatId") Long chatId,
        @Param("startX") Integer startX,
        @Param("endX") Integer endX,
        @Param("startY") Integer startY,
        @Param("endY") Integer endY
    );

    List<Message> findAllByChatIdAndXBetweenAndYBetweenAndEditedBeforeOrderByEditedDesc(
        Long chatId,
        Integer startX,
        Integer endX,
        Integer startY,
        Integer endY,
        LocalDateTime endTime
    );

    List<Message> findAllByChatIdAndXBetweenAndYBetweenAndEditedBetweenOrderByEditedDesc(
        Long chatId,
        Integer startX,
        Integer endX,
        Integer startY,
        Integer endY,
        LocalDateTime startTime,
        LocalDateTime endTime
    );

    List<Message> findAllByChatIdAndXBetweenAndYBetweenAndEditedAfterOrderByEditedDesc(
        Long chatId,
        Integer startX,
        Integer endX,
        Integer startY,
        Integer endY,
        LocalDateTime startTime
    );
}
