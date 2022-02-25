package ru.comgrid.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.comgrid.server.model.CellUnion;

public interface MessageUnionRepository extends PagingAndSortingRepository<CellUnion, Long>{

    @Query("""
            select c from CellUnion c
            where c.chatId = :chatId and
            c.leftTopx >= :xCoordLeftTop and c.leftTopy >= :yCoordLeftTop and
            c.rightDownx <= :xCoordRightBottom and c.rightDowny <= :yCoordRightBottom
           """)
    Page<CellUnion> findAllByChat(
        Long chatId,
        int xCoordLeftTop,
        int yCoordLeftTop,
        int xCoordRightBottom,
        int yCoordRightBottom,
        Pageable pageable
    );
}
