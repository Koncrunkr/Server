package ru.comgrid.server.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.comgrid.server.model.Chat;

import java.math.BigDecimal;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long>{
	@SuppressWarnings("SpringDataRepositoryMethodParametersInspection")
	@Override
	@Query("select c from Chat c where c.id = :ids")
	@NotNull
	List<Chat> findAllById(@Param("ids") @NotNull Iterable<Long> ids);

	@Query("""
		select c from Chat c
			inner join TableParticipants tp on tp.chat=c.id
			left join Message m on c.lastMessageX=m.x and c.lastMessageY=m.y and m.chatId=c.id
		where tp.person=:personId
		order by m.created desc nulls last
		""")
	List<Chat> findAllChatsByPerson(@NotNull BigDecimal personId);
}
