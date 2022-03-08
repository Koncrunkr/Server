package ru.comgrid.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.comgrid.server.model.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
