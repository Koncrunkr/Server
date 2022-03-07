package ru.comgrid.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.comgrid.server.model.Person;
import ru.comgrid.server.repository.PersonRepository;

import java.util.List;

@SpringBootApplication
public class ServerApplication {
	public static void main(String[] args) throws ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
		SpringApplication.run(ServerApplication.class, args);
	}
}
