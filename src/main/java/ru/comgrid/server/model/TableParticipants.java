package ru.comgrid.server.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.springframework.data.domain.Persistable;
import ru.comgrid.server.util.EnumSet0;
import ru.comgrid.server.util.RegularEnumSet0;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;


@Entity
@IdClass(TableParticipant.class)
@NoArgsConstructor
public class TableParticipants implements Serializable, Persistable<TableParticipant>{
    @Id
    @Getter
    private Long chat;
    @Id
    @Column(precision = 40)
    @JsonSerialize(using = ToStringSerializer.class)
    @Getter
    private BigDecimal person;

    @Transient
    private EnumSet0<Right> _rights;

    @Column
    private long rights;

    @Column
    private LocalDateTime lastTimeSeen;

    public long getRights(){
        return rights;
    }

    public void setRights(long rights){
        this._rights = EnumSet0.of(Right.class, rights);
        this.rights = rights;
    }

    @Transient
    public EnumSet0<Right> rights(){
        return _rights != null ? _rights : (_rights = EnumSet0.of(Right.class, rights));
    }

    @Override
    public TableParticipant getId(){
        return new TableParticipant(chat, person);
    }

    public TableParticipants(Long chat, BigDecimal person, long rights, LocalDateTime lastTimeSeen){
        this.chat = chat;
        this.person = person;
        setRights(rights);
        this.lastTimeSeen = lastTimeSeen;
    }
    public TableParticipants(Long chat, BigDecimal person, EnumSet0<Right> rights, LocalDateTime lastTimeSeen){
        this.chat = chat;
        this.person = person;
        this._rights = rights;
        this.rights = ((RegularEnumSet0<Right>) rights).elements();
        this.lastTimeSeen = lastTimeSeen;
    }

    @Override
    public boolean isNew(){
        return true;
    }


}