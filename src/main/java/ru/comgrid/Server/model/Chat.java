package ru.comgrid.Server.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Chat implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(length = 50)
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String creator;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Chat() {

    }

    public Chat(String id, String creator, String name) {
        this.creator = creator;
        this.name = name;
    }
}
