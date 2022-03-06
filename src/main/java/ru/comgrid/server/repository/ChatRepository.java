package ru.comgrid.server.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.comgrid.server.model.Chat;

public interface ChatRepository extends CrudRepository<Chat, Long>{
}
