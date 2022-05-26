package ru.comgrid.server.model;

import javax.persistence.*;

@Entity
public class InnerFile{

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = true)
    private String name;

    @Lob
    @Column(nullable = false, unique = true)
    private String link;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private FileType type;
}
