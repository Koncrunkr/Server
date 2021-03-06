package ru.comgrid.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.comgrid.server.model.CellUnion;

import java.util.List;

public interface CellUnionRepository extends JpaRepository<CellUnion, Long>{

    @Query("""
            select c from CellUnion c
            where c.chatId = :chatId and
            c.xcoordRightBottom >= :xcoordLeftTop and c.ycoordRightBottom >= :ycoordLeftTop and
            c.xcoordLeftTop <= :xcoordRightBottom and c.ycoordLeftTop <= :ycoordRightBottom
           """)
    List<CellUnion> findAllByChat(
        Long chatId,
        int xcoordLeftTop,
        int ycoordLeftTop,
        int xcoordRightBottom,
        int ycoordRightBottom
    );

    @Query("""
            select count(c) > 0 from CellUnion c
                where c.chatId = :chatId and
                c.xcoordRightBottom >= :xcoordLeftTop and c.ycoordRightBottom >= :ycoordLeftTop and
                c.xcoordLeftTop <= :xcoordRightBottom and c.ycoordLeftTop <= :ycoordRightBottom
            """)
    boolean existsCellUnion(
        Long chatId,
        int xcoordLeftTop,
        int ycoordLeftTop,
        int xcoordRightBottom,
        int ycoordRightBottom
    );
}
