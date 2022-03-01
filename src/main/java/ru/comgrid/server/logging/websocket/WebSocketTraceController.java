package ru.comgrid.server.logging.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Queue;

@RestController
@ConditionalOnProperty(prefix = "ru.comgrid.websocket.trace", name = "enabled")
public class WebSocketTraceController{

	private final WebSocketTraceRepository webSocketTraceRepository;

	public WebSocketTraceController(@Autowired WebSocketTraceRepository webSocketTraceRepository){
		this.webSocketTraceRepository = webSocketTraceRepository;
	}

	@GetMapping("/websockettrace")
	public WebSocketTraceDescriptor traces(){
		System.out.println("Access");
		return new WebSocketTraceDescriptor(this.webSocketTraceRepository.findAll());
	}

	/**
	 * A description of an application's {@link WebSocketTraceRepository} entries. Primarily intended for
	 * serialization to JSON.
	 */
	record WebSocketTraceDescriptor(Queue<WebSocketTrace> traces){
		public Queue<WebSocketTrace> getTraces(){
			return this.traces;
		}
	}
}
