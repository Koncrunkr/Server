package ru.comgrid.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.comgrid.server.model.Person;

import java.math.BigDecimal;
import java.math.BigInteger;

@Repository
public interface PersonRepository extends PagingAndSortingRepository<Person, String> {

    @Query("SELECT p FROM Person p " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "ORDER BY p.name")
    Page<Person> findPeople(
            @Param("searchTerm") String searchTerm,
            Pageable pageRequest
    );

    Person findById(BigInteger id);
    boolean existsById(BigInteger id);

}
