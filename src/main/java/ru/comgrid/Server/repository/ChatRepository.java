package ru.comgrid.Server.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.comgrid.Server.model.Chat;

public interface ChatRepository extends PagingAndSortingRepository<Chat, Long> {

}
