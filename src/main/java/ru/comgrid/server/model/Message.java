package ru.comgrid.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Persistable;
import ru.comgrid.server.api.user.UserHelp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <pre>
| field             | includes | description                                      |
|-------------------|----------|--------------------------------------------------|
| id: integer       | always   | unique id of message                             |
| x: integer        | always   | x coordinate of message                          |
| y: integer        | always   | y coordinate of message                          |
| chatId: integer   | always   | chatId this message corresponds to               |
| time: integer     | always   | time when this message was sent(since 1.01.1970) |
| senderId: integer | always   | unique sender's id                               |
| text: string      | always   | content of message                               |
 * </pre>
 */
@Entity
@ToString
@Getter
@Setter
@IdClass(MessageId.class)
@NoArgsConstructor
public class Message implements Serializable, Persistable<MessageId>{

    @Id
    @Column(nullable = false)
    private Integer x;

    @Id
    @Column(nullable = false)
    private Integer y;

    @Id
    @Column(nullable = false)
    private Long chatId;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime edited;

    @Column(nullable = false, precision = 40)
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal senderId;

    @Column(nullable = false, columnDefinition = "text not null")
    private String text;

    @OneToOne
    private InnerFile file;

    @Transient
    @JsonIgnore
    private boolean isNew;

    public MessageId getId(){
        return new MessageId(chatId, x, y);
    }

    public Message(Integer x, Integer y, Long chatId, LocalDateTime created, BigDecimal senderId, String text){
        this.x = x;
        this.y = y;
        this.chatId = chatId;
        this.created = created;
        this.edited = created;
        this.senderId = senderId;
        this.text = text;
    }

    public boolean isSender(BigDecimal personId){
        return UserHelp.samePerson(personId, this.senderId);
    }
}
