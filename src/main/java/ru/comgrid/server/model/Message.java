package ru.comgrid.server.model;

import lombok.Getter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
public class Message implements Serializable {
    @EmbeddedId
    private MessageId messageId;

    @Getter
    private LocalDateTime time;

    @Getter
    private BigInteger senderId;

    @Getter
    private String text;

    public Message(MessageId messageId, LocalDateTime time, BigInteger senderId, String text){
        this.messageId = messageId;
        this.time = time;
        this.senderId = senderId;
        this.text = text;
    }

    public Message() {
    }
}
