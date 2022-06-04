package ru.comgrid.server.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


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
 | created: integer   | always   | time in millis            |
 | participants: list | optional | list consisting of {@link Person} objects |
 * </pre>
 */
@Entity
@ToString
@Getter
@Setter
public class Chat implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(precision = 40, nullable = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal creator;

    @Column(nullable = false)
    private Integer width;

    @Column(nullable = false)
    private Integer height;

    @Schema(defaultValue = "url")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "avatar_id", referencedColumnName = "id")
    private InnerFile avatar;

    @Column(nullable = false)
    private LocalDateTime created;

    @Getter
    @Setter
    private Integer lastMessageX;

    @Getter
    @Setter
    private Integer lastMessageY;

    @Transient
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
        this.avatar = new InnerFile("avatar", avatar, FileType.IMAGE);
    }

    public void setLastMessageId(MessageId messageId){
        this.lastMessageX = messageId.getX();
        this.lastMessageY = messageId.getY();
    }
}
