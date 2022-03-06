package ru.comgrid.server.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;
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
public class Message implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false)
    private Integer x;

    @Getter
    @Setter
    @Column(nullable = false)
    private Integer y;

    @Getter
    @Setter
    @Column(nullable = false)
    private Long chatId;

    @Getter
    @Column(nullable = false)
    @Setter
    private LocalDateTime time;

    @Getter
    @Column(nullable = false, precision = 40)
    @JsonSerialize(using = ToStringSerializer.class)
    @Setter
    private BigDecimal senderId;

    @Getter
    @Column(nullable = false)
    private String text;

    public Message(Integer x, Integer y, Long chatId, LocalDateTime time, BigDecimal senderId, String text){
        this.x = x;
        this.y = y;
        this.chatId = chatId;
        this.time = time;
        this.senderId = senderId;
        this.text = text;
    }

    public boolean isSender(BigDecimal personId){
        return UserHelp.samePerson(personId, this.senderId);
    }
    public Message() {
    }
}
