package ru.comgrid.server.model;

import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;


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

    @Transient
    private EnumSet<Right> rights;

    private LocalDateTime lastTimeSeen;

    @Transient
    public EnumSet<Right> rights(){
        return rights;
    }

    private static final Field rightsField = getField();

    @Override
    public TableParticipant getId(){
        return new TableParticipant(chat, person);
    }

    @Override
    public boolean isNew(){
        return true;
    }

    // I don't know why java didn't make access to the long value inside.
    // Maybe they will change this in future versions and
    @SneakyThrows
    private static Field getField(){
        return EnumSet.noneOf(Right.class).getClass().getDeclaredField("elements");
    }
    @Column
    @SneakyThrows
    public long getRights(){
        return rightsField.getLong(rights);
    }
    @SneakyThrows
    public void setRights(long rights){
        this.rights = EnumSet.noneOf(Right.class);
        rightsField.setLong(this.rights, rights);
    }
}