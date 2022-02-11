package ru.comgrid.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.comgrid.server.model.TableParticipant;
import ru.comgrid.server.model.TableParticipants;

import java.math.BigInteger;

public interface ChatParticipantsRepository extends PagingAndSortingRepository<TableParticipants, TableParticipant>{
    Page<Long> findAllByPerson(BigInteger personId, Pageable pageable);
    Page<BigIntegerProjection> findAllByChat(Long chatId, Pageable pageable);

    default Page<BigInteger> findAllByTableUnwrapped(Long tableId, Pageable pageable){
        var allByTable = findAllByChat(tableId, pageable);
        return allByTable.map(BigIntegerProjection::value);
    }
}
