package ru.comgrid.Server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {
	public static void main(String[] args) throws ClassNotFoundException {
		SpringApplication.run(ServerApplication.class, args);
		Class.forName("org.postgresql.Driver");
	}
}
