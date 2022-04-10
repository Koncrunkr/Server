package ru.comgrid.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.comgrid.server.model.Decoration;

import java.util.List;

public interface DecorationRepository extends JpaRepository<Decoration, Long>{

	@Query("select d from Decoration d where d.cellUnionId in :cellUnionIds and d.chatId = :chatId")
	List<Decoration> findAllByCellUnionIdInAndAndChatId(@Param("cellUnionIds") List<Long> cellUnionIds, @Param("chatId") Long chatId);
}
