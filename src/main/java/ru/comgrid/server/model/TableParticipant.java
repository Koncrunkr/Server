package ru.comgrid.server.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
public class TableParticipant implements Serializable{
    private Long chat;
    private BigDecimal person;
}