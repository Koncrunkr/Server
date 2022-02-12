package ru.comgrid.server.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private Integer xCoord;

    @Getter
    @Setter
    @Column(nullable = false)
    private Integer yCoord;

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
    @Setter
    private BigDecimal senderId;

    @Getter
    @Column(nullable = false)
    private String text;

    public Message(Integer xCoord, Integer yCoord, Long chatId, LocalDateTime time, BigDecimal senderId, String text){
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.chatId = chatId;
        this.time = time;
        this.senderId = senderId;
        this.text = text;
    }


    public Message() {
    }
}
