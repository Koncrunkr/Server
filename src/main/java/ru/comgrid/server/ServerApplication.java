package ru.comgrid.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import ru.comgrid.server.security.AppProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableWebSocket
public class ServerApplication {
	public static void main(String[] args) throws ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
		SpringApplication.run(ServerApplication.class, args);
	}
}
