package ru.comgrid.server.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.comgrid.server.model.Chat;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
	@SuppressWarnings("SpringDataRepositoryMethodParametersInspection")
	@Override
	@NotNull
	List<Chat> findAllById(@NotNull Iterable<Long> ids);
}
