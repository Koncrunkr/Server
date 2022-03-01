package ru.comgrid.server.logging.websocket;

import java.util.Queue;

public interface WebSocketTraceRepository{

	Queue<WebSocketTrace> findAll();
	void add(WebSocketTrace trace);
}
