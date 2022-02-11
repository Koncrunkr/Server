package ru.comgrid.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;


@Entity
@IdClass(TableParticipant.class)
@AllArgsConstructor
@NoArgsConstructor
public class TableParticipants implements Serializable {
    @Id
    @Getter
    private Long chat;
    @Id
    @Column(length = 50)
    @Getter
    private BigInteger person;
}