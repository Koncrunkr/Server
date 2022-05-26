package ru.comgrid.server.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@ToString
@Getter
@Setter
public class CellUnion{
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long chatId;

    @Column(nullable = false, precision = 40)
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
