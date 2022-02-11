package ru.comgrid.server.model;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

@Entity
public class Chat implements Serializable, Jsonable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(length = 50)
    @Getter
    @Setter
    private String name;

    @Column(length = 40)
    @Getter
    @Setter
    private BigInteger creator;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Chat() {

    }

    public Chat(String id, BigInteger creator, String name) {
        this.creator = creator;
        this.name = name;
    }

    @Override
    public JsonObject toJson(){
        return null;
    }
}
