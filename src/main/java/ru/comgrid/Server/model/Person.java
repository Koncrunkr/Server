package ru.comgrid.Server.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "persons")
public class Person implements Serializable {
    @Id
    @Column
    @GenericGenerator(name = "uuid", strategy = "uuid4")
    private String id;

    @Column(length = 50)
    @Getter
    private String name;

    @Column(unique = true, nullable = false)
    @Getter
    @Setter
    private String email;

    @Column(length = 60, nullable = false)
    @Getter
    @Setter
    private String password;

    @Temporal(TemporalType.DATE)
    @Getter
    @Setter
    private Date birthDate;

    @Column(updatable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    private final Date created = new Date();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Person(String id, String shortName, String email, String password, Date birthDate) {
        this.id = id;
        this.name = shortName;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
    }

    public Person() {

    }
}
