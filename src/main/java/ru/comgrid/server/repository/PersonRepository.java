package ru.comgrid.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.comgrid.server.model.Person;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, BigDecimal> {

    @Query("SELECT p FROM Person p " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "ORDER BY p.name")
    List<Person> findPeople(@Param("searchTerm") String searchTerm);

    @Query("select (count(p) > 0) from Person p where p.id = :id")
    boolean existsById(@Param("id") @NonNull BigDecimal id);
}
