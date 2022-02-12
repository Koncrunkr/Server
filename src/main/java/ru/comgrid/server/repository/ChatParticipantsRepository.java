package ru.comgrid.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.comgrid.server.model.Person;
import ru.comgrid.server.model.TableParticipant;
import ru.comgrid.server.model.TableParticipants;

import java.math.BigDecimal;

public interface ChatParticipantsRepository extends PagingAndSortingRepository<TableParticipants, TableParticipant>{
    Page<Long> findAllByPerson(BigDecimal personId, Pageable pageable);
    Page<BigDecimalProjection> findAllByChat(Long chatId, Pageable pageable);

    boolean existsByChatAndPerson(Long chatId, BigDecimal personId);

    default Page<BigDecimal> findAllByTableUnwrapped(Long tableId, Pageable pageable){
        var allByTable = findAllByChat(tableId, pageable);
        return allByTable.map(BigDecimalProjection::value);
    }
}
