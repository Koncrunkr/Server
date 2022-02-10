package ru.comgrid.Server.model;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

public class ChatParticipants implements Serializable {
    @Id
    private String chat;
    @Id
    private String person;
}
