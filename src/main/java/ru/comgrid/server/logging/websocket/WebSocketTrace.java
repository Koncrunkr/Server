package ru.comgrid.server.logging.websocket;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@AllArgsConstructor
@Getter
public final class WebSocketTrace{
	private final Instant timestamp = Instant.now();

	private String sessionId;

	private String stompCommand;

	private Map<String, Object> nativeHeaders;

	private String payload;
}
