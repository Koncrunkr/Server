package ru.comgrid.Server.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
public class Message implements Serializable {
    @EmbeddedId
    private MessageId messageId;

    @Getter
    @Setter
    private String body;

    public Message(MessageId messageId, String body) {
        this.messageId = messageId;
        this.body = body;
    }

    public Message() {
    }
}
