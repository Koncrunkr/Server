package ru.comgrid.server.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;
import ru.comgrid.server.service.Provider;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;


/**
 * Person. It has:
 * <pre>
 | field           | includes | how to include           |
 |-----------------|----------|:-------------------------|
 | id: biginteger  | always   |                          |
 | name: string    | always   |                          |
 | email: string   | always   |                          |
 | avatar: string  | always   |                          |
 | chats: {@link Chat}[] | optional | param includeChats=true |
 * </pre>
 */
@Entity
public class Person implements Serializable, Persistable<BigInteger>, Jsonable{
    @Id
    @Getter
    @Column(precision = 40)
    private BigInteger id;

    @Column(length = 50, nullable = false)
    @Getter
    @Setter
    private String name;

    @Column(unique = true, nullable = false)
    @Getter
    @Setter
    private String email;

    @Column(nullable = false)
    @Getter
    @Setter
    private String avatar;

    // We don't use passwords yet. Even if we will it doesn't mean we will store them raw.
//    @Column(length = 60, nullable = false)
//    @Getter
//    @Setter
//    private String password;

//    @Temporal(TemporalType.DATE)
//    @Getter
//    @Setter
//    private Date birthDate;

    @Column(updatable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    private final Date created = new Date();

    @Transient
    @Setter
    @Getter
    private boolean isNew;

    @Enumerated(EnumType.STRING)
    @Setter
    @Getter
    private Provider provider = Provider.GOOGLE;

    @Transient
    @Setter
    @Getter
    private Iterable<Chat> chats = null;


    public Person(BigInteger id, String name, String email, String avatar) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.isNew = false;
    }

    public Person() {}

    @Override
    public JsonObject toJson(){
        var json = new JsonObject();
        json.addProperty("id", id.toString());
        json.addProperty("name", name);
        json.addProperty("email", email);
        json.addProperty("avatar", avatar);

        if(chats != null){
            JsonArray jsonChats = new JsonArray();
            for(Chat chat : chats){
                jsonChats.add(chat.toJson());
            }
            json.add("chats", jsonChats);
        }
        return json;
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
