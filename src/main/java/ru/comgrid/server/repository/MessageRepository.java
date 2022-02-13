package ru.comgrid.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.comgrid.server.model.Message;

import java.time.LocalDateTime;

public interface MessageRepository extends PagingAndSortingRepository<Message, Long> {
    Page<Message> findAllByChatIdAndXBetweenAndYBetweenOrderByTimeDesc(
        Long chatId,
        Integer startX,
        Integer endX,
        Integer startY,
        Integer endY,
        Pageable pageable
    );

    Page<Message> findAllByChatIdAndXBetweenAndYBetweenAndTimeBeforeOrderByTimeDesc(
        Long chatId,
        Integer startX,
        Integer endX,
        Integer startY,
        Integer endY,
        LocalDateTime endTime,
        Pageable pageable
    );

    Page<Message> findAllByChatIdAndXBetweenAndYBetweenAndTimeBetweenOrderByTimeDesc(
        Long chatId,
        Integer startX,
        Integer endX,
        Integer startY,
        Integer endY,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Pageable pageable
    );

    Page<Message> findAllByChatIdAndXBetweenAndYBetweenAndTimeAfterOrderByTimeDesc(
        Long chatId,
        Integer startX,
        Integer endX,
        Integer startY,
        Integer endY,
        LocalDateTime startTime,
        Pageable pageable
    );
}
