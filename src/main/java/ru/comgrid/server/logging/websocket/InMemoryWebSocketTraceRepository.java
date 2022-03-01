package ru.comgrid.server.logging.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryWebSocketTraceRepository implements WebSocketTraceRepository{
	private final int capacity;
	private final ArrayDeque<WebSocketTrace> traces;

	public InMemoryWebSocketTraceRepository(@Value("${ru.comgrid.websocket.trace.max-count}") int capacity){
		this.capacity = capacity;
		this.traces = new ArrayDeque<>(capacity);
	}

	@Override
	public ArrayDeque<WebSocketTrace> findAll(){
		synchronized(this.traces){
			return traces.clone();
		}
	}

	@Override
	public void add(WebSocketTrace trace){
		synchronized(this.traces){
			if(traces.size() == capacity){
				traces.poll();
			}
			traces.addLast(trace);
		}
	}
}
