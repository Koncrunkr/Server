package ru.comgrid.Server.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.comgrid.Server.model.Person;

public interface MessageRepository extends PagingAndSortingRepository<Person, String> {
}
