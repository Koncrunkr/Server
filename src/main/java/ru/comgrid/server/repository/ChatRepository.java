package ru.comgrid.server.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.comgrid.server.model.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
