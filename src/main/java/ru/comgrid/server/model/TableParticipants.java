package ru.comgrid.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;


@Entity
@IdClass(TableParticipant.class)
@AllArgsConstructor
@NoArgsConstructor
public class TableParticipants implements Serializable, Persistable<TableParticipant>{
    @Id
    @Getter
    private Long chat;
    @Id
    @Column(precision = 40)
    @Getter
    private BigDecimal person;

    @Override
    public TableParticipant getId(){
        return new TableParticipant(chat, person);
    }

    @Override
    public boolean isNew(){
        return true;
    }
}