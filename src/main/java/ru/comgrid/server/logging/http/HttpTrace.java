package ru.comgrid.server.logging.http;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpHeaders;
import ru.comgrid.server.security.user.info.UserPrincipal;

import java.net.URI;
import java.time.Instant;

public record HttpTrace(
	Request request,
	Response response,
	Object principal,
	Instant timestamp,
	long timeTaken
){

	public record Request(
		String method,
		URI uri,
		HttpHeaders httpHeaders,
		JsonNode body,
		String remoteAddr
	){ }

	public record Response(
		int status,
		JsonNode body,
		HttpHeaders headers
	){

	}
}
