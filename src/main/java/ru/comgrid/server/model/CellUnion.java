package ru.comgrid.server.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class CellUnion{
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long chatId;

    @Column(nullable = false)
    private BigDecimal creatorId;

    @Column(nullable = false)
    private Integer xcoordLeftTop;

    @Column(nullable = false)
    private Integer ycoordLeftTop;

    @Column(nullable = false)
    private Integer xcoordRightBottom;

    @Column(nullable = false)
    private Integer ycoordRightBottom;
}
