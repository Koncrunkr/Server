package ru.comgrid.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.comgrid.server.model.Message;
import ru.comgrid.server.model.Person;

import java.time.LocalDateTime;

public interface MessageRepository extends PagingAndSortingRepository<Message, Long> {
    Page<Message> findAllByChatIdAndXCoordBetweenAndYCoordBetweenOrderByTimeDesc(
        Long chatId,
        Integer startX,
        Integer endX,
        Integer startY,
        Integer endY,
        Pageable pageable
    );

    Page<Message> findAllByChatIdAndXCoordBetweenAndYCoordBetweenAndTimeBeforeOrderByTimeDesc(
        Long chatId,
        Integer startX,
        Integer endX,
        Integer startY,
        Integer endY,
        LocalDateTime endTime,
        Pageable pageable
    );

    Page<Message> findAllByChatIdAndXCoordBetweenAndYCoordBetweenAndTimeBetweenOrderByTimeDesc(
        Long chatId,
        Integer startX,
        Integer endX,
        Integer startY,
        Integer endY,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Pageable pageable
    );

    Page<Message> findAllByChatIdAndXCoordBetweenAndYCoordBetweenAndTimeAfterOrderByTimeDesc(
        Long chatId,
        Integer startX,
        Integer endX,
        Integer startY,
        Integer endY,
        LocalDateTime startTime,
        Pageable pageable
    );
}
