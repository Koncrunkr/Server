package ru.comgrid.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.comgrid.server.service.Provider;
import ru.comgrid.server.util.GrantedAuthorityConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
@Getter
@Setter
public class Person implements Serializable, Persistable<BigDecimal>{
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    @Column(precision = 40)
    private BigDecimal id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    @Schema(defaultValue = "url")
    private InnerFile avatar;

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
    private final LocalDateTime created = LocalDateTime.now();

    @JsonIgnore
    @Transient
    private boolean isNew;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Transient
    private List<Chat> chats = null;

    @ElementCollection(fetch = FetchType.EAGER)
    @Convert(converter = GrantedAuthorityConverter.class)
    private List<GrantedAuthority> authorities;

	public Person(BigDecimal id, String name, String email, InnerFile avatar, Provider provider){
		this.id = id;
		this.name = name;
		this.email = email;
		this.avatar = avatar;
		this.provider = provider;
		this.isNew = false;
	}

    public Person() {}
}
