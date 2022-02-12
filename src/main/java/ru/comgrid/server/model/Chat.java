package ru.comgrid.server.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


/**
 * Chat has:
 * <pre>
 | field              | includes | description                      |
 |--------------------|----------|----------------------------------|
 | id: integer        | always   | unique id of table(chat)         |
 | name: string       | always   | name of chat                     |
 | creator: string    | always   | chat's creator's unique id       |
 | width: integer     | always   | width of chat in cells           |
 | height: integer    | always   | height of chat in cells          |
 | avatar: string     | always   | link to avatar of chat           |
 | participants: list | optional | list consisting of {@link Person} objects |
 * </pre>
 */
@Entity
public class Chat implements Serializable, Jsonable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(length = 50, nullable = false)
    @Getter
    @Setter
    private String name;

    @Column(precision = 40, nullable = false)
    @Getter
    @Setter
    private BigDecimal creator;

    @Getter
    @Setter
    @Column(nullable = false)
    private Integer width;

    @Getter
    @Setter
    @Column(nullable = false)
    private Integer height;

    @Getter
    @Setter
    @Column(nullable = false)
    private String avatar;

    @Getter
    @Setter
    @Column
    private LocalDateTime created;

    @Transient
    @Getter
    @Setter
    private Iterable<Person> participants = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Chat() {

    }

    public Chat(BigDecimal creator, String name, Integer width, Integer height, String avatar) {
        this.creator = creator;
        this.name = name;
        this.width = width;
        this.height = height;
        this.avatar = avatar;
    }

    @Override
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", id);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("creator", creator.toString());
        jsonObject.addProperty("width", width);
        jsonObject.addProperty("height", height);
        jsonObject.addProperty("avatar", avatar);
        jsonObject.addProperty("created", created.getNano()/1_000_000 + created.toEpochSecond(ZoneOffset.UTC));

        if(participants != null){
            JsonArray participantsJson = new JsonArray();
            for (var participant : participants){
                participantsJson.add(participant.toJson());
            }
            jsonObject.add("participants", participantsJson);
        }

        return jsonObject;
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
