package ru.comgrid.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.comgrid.server.model.Person;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, BigDecimal> {

    @Query(nativeQuery = true,
        value = "SELECT p.id, p.username, p.avatar, p.name FROM person p " +
        "WHERE p.username ~* ('%' || :username || '%') " +
        "ORDER BY p.username")
    List<Person> findPeople(@Param("username") String username);

    @Query("select (count(p) > 0) from Person p where p.id = :id")
    boolean existsById(@Param("id") @NonNull BigDecimal id);
}
