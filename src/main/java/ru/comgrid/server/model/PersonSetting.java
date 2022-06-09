package ru.comgrid.server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class PersonSetting{
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private BigDecimal personId;
    @Column(nullable = false)
    private String setting;
    @Column(nullable = false)
    private String value;

    public PersonSetting(BigDecimal personId, String setting){
        this.personId = personId;
        this.setting = setting;
    }
}
