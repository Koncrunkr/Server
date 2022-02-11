package ru.comgrid.server.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.comgrid.server.model.Person;

public interface MessageRepository extends PagingAndSortingRepository<Person, String> {

}
