package ru.comgrid.server.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;
import ru.comgrid.server.service.Provider;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * Person. It has:
 * <pre>
 | field           | includes | how to include          | description                                           |
 |-----------------|----------|:------------------------|-------------------------------------------------------|
 | id: string      | always   |                         | unique person id consisting of digits                 |
 | name: string    | always   |                         | name of user                                          |
 | email: string   | always   |                         | email of user                                         |
 | avatar: string  | always   |                         | link to avatar of user                                |
 | chats: {@link Chat}[]   | optional | param includeChats=true | list of {@link Chat}s, that this user participates in         |
 * </pre>
 */
@Entity
public class Person implements Serializable, Persistable<BigDecimal>{
    @Id
    @Getter
    @JsonSerialize(using = ToStringSerializer.class)
    @Column(precision = 40)
    private BigDecimal id;

    @Column(length = 50, nullable = false)
    @Getter
    @Setter
    private String name;

    @Column(unique = true, nullable = false)
    @Getter
    @Setter
    private String email;

    @Column(nullable = false)
    @Schema(defaultValue = "url")
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

    @JsonIgnore
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


    public Person(BigDecimal id, String name, String email, String avatar) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.isNew = false;
    }

    public Person() {}
}
