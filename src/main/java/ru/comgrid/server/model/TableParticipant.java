package ru.comgrid.server.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal person;
}