package ru.comgrid.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.comgrid.server.model.CellUnion;

public interface CellUnionRepository extends PagingAndSortingRepository<CellUnion, Long>{

    @Query("""
            select c from CellUnion c
            where c.chatId = :chatId and
            c.xcoordRightBottom >= :xcoordLeftTop and c.ycoordRightBottom >= :ycoordLeftTop and
            c.xcoordLeftTop <= :xcoordRightBottom and c.ycoordLeftTop <= :ycoordRightBottom
           """)
    Page<CellUnion> findAllByChat(
        Long chatId,
        int xcoordLeftTop,
        int ycoordLeftTop,
        int xcoordRightBottom,
        int ycoordRightBottom,
        Pageable pageable
    );
}