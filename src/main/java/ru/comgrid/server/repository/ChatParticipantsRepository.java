package ru.comgrid.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ru.comgrid.server.model.Person;
import ru.comgrid.server.model.TableParticipant;
import ru.comgrid.server.model.TableParticipants;

import java.math.BigDecimal;

public interface ChatParticipantsRepository extends PagingAndSortingRepository<TableParticipants, TableParticipant>{
    @Query("select t.chat from TableParticipants t where t.person = :personId")
    Page<Long> findAllChatsByPerson(@Param("personId") BigDecimal person, Pageable pageable);
    @Query("select t.person from TableParticipants t where t.chat = :chatId")
    Page<BigDecimal> findAllByChat(@Param("chatId") Long chatId, Pageable pageable);

    @Query("select (count(t) > 0) from TableParticipants t where t.chat = :chatId and t.person = :personId")
    boolean existsByChatAndPerson(@Param("chatId") Long chatId, @Param("personId") BigDecimal personId);

    @Query("select t from TableParticipants t where t.person = :personId")
    TableParticipants findByPerson(@Param("personId") BigDecimal personId);

}
