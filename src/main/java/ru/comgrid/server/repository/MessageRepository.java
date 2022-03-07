package ru.comgrid.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ru.comgrid.server.model.Message;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long>{
    Optional<Message> findMessageByChatIdAndXAndY(@Param("chatId") Long chatId, @Param("x") Integer x, @Param("y") Integer y);

    Page<Message> findAllByChatIdAndXBetweenAndYBetweenOrderByTimeDesc(
        @Param("chatId") Long chatId,
        @Param("startX") Integer startX,
        @Param("endX") Integer endX,
        @Param("startY") Integer startY,
        @Param("endY") Integer endY
    );

    Page<Message> findAllByChatIdAndXBetweenAndYBetweenAndTimeBeforeOrderByTimeDesc(
        Long chatId,
        Integer startX,
        Integer endX,
        Integer startY,
        Integer endY,
        LocalDateTime endTime
    );

    Page<Message> findAllByChatIdAndXBetweenAndYBetweenAndTimeBetweenOrderByTimeDesc(
        Long chatId,
        Integer startX,
        Integer endX,
        Integer startY,
        Integer endY,
        LocalDateTime startTime,
        LocalDateTime endTime
    );

    Page<Message> findAllByChatIdAndXBetweenAndYBetweenAndTimeAfterOrderByTimeDesc(
        Long chatId,
        Integer startX,
        Integer endX,
        Integer startY,
        Integer endY,
        LocalDateTime startTime
    );
}
