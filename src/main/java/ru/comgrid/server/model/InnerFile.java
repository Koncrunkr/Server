package ru.comgrid.server.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
public class InnerFile{
    @Id
    @GeneratedValue
    private Long id;

    @Column()
    private String name;

    @Column(nullable = false, unique = true)
    private String link;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private FileType type;

    public InnerFile(String name, String link, FileType type){
        this.name = name;
        this.link = link;
        this.type = type;
    }

    public InnerFile(){

    }

}
