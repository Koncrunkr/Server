package ru.comgrid.server.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TableParticipant implements Serializable{
    private Long chat;
    private BigDecimal person;
}