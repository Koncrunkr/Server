package ru.comgrid.server.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TableParticipant implements Serializable{
//    @ManyToOne(targetEntity = Chat.class, optional = false)
    private Long chat;
//    @ManyToOne(targetEntity = Person.class, optional = false)
    private BigDecimal person;
}