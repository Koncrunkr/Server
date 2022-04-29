package ru.comgrid.server.logging.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.comgrid.server.logging.TraceRepository;

import java.util.Collection;

import static ru.comgrid.server.security.UserRole.ROLE_ADMIN;

@RestController
@ConditionalOnProperty(prefix = "ru.comgrid.websocket", name = "trace-enabled")
public class WebsocketTraceController{
	private final TraceRepository<WebsocketTrace> webSocketTraceRepository;

	public WebsocketTraceController(@Autowired TraceRepository<WebsocketTrace> webSocketTraceRepository){
		this.webSocketTraceRepository = webSocketTraceRepository;
	}

	@GetMapping("/websockettrace")
	@Secured(ROLE_ADMIN)
	public WebsocketTraceDescriptor traces(){
		return new WebsocketTraceDescriptor(this.webSocketTraceRepository.findAll());
	}

	/**
	 * A description of an application's {@link TraceRepository} entries. Primarily intended for
	 * serialization to JSON.
	 */
	record WebsocketTraceDescriptor(Collection<WebsocketTrace> traces){
		public Collection<WebsocketTrace> getTraces(){
			return this.traces;
		}
	}
}
