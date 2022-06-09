package ru.comgrid.server.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.comgrid.server.model.PersonSetting;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PersonSettingRepository extends CrudRepository<PersonSetting, Long>{

    List<PersonSetting> findAllByPersonId(@Param("personId") BigDecimal personId);

    Optional<PersonSetting> findByPersonIdAndSetting(@Param("personId") BigDecimal personId, @Param("setting") String setting);
}
