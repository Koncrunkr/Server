package ru.comgrid.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ru.comgrid.server.model.Person;
import ru.comgrid.server.model.TableParticipant;
import ru.comgrid.server.model.TableParticipants;

import java.math.BigDecimal;
import java.util.List;

public interface ChatParticipantsRepository extends JpaRepository<TableParticipants, TableParticipant> {
    @Query("select t.chat from TableParticipants t where t.person = :personId")
    List<Long> findAllChatsByPerson(@Param("personId") BigDecimal person);

    @Query("select t.person from TableParticipants t where t.chat = :chatId")
    List<BigDecimal> findAllByChat(@Param("chatId") Long chatId);

    boolean existsByChatAndPerson(@Param("chatId") Long chatId, @Param("personId") BigDecimal personId);

    TableParticipants findByPerson(@Param("personId") BigDecimal personId);

}
