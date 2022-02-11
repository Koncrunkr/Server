package ru.comgrid.server.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.comgrid.server.model.Chat;

public interface ChatRepository extends PagingAndSortingRepository<Chat, Long> {
}
