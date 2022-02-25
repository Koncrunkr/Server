package ru.comgrid.server.model;

import javax.persistence.*;

@Entity
public class CellUnion{
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long chatId;

    @Column(nullable = false)
    private Long leftTopx;

    @Column(nullable = false)
    private Long leftTopy;

    @Column(nullable = false)
    private Long rightDownx;

    @Column(nullable = false)
    private Long rightDowny;
}
