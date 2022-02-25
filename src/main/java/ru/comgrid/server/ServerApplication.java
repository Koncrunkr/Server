package ru.comgrid.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.comgrid.server.model.Person;
import ru.comgrid.server.repository.PersonRepository;

@SpringBootApplication
public class ServerApplication {
	public static void main(String[] args) throws ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
		SpringApplication.run(ServerApplication.class, args);
	}
	@Bean
	public CommandLineRunner runner(PersonRepository personRepository){
		return (args) -> {
			Page<Person> people = personRepository.findPeople(
				"\"privet\", '%')); drop table Person;",
				Pageable.unpaged()
			);
			System.out.println(people.getContent());
		};
	}
}
