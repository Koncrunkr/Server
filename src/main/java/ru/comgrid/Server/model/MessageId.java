package ru.comgrid.Server.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class MessageId implements Serializable {
    @Getter
    @Setter
    private Integer x;

    @Getter
    @Setter
    private Integer y;

    @Getter
    @Setter
    private String chatId;

    public MessageId(Integer x, Integer y, String chatId) {
        this.x = x;
        this.y = y;
        this.chatId = chatId;
    }

    public MessageId() {

    }
}
