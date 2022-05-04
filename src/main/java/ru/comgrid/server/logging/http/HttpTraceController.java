package ru.comgrid.server.logging.http;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.comgrid.server.logging.TraceRepository;

import java.util.Collection;

import static ru.comgrid.server.security.UserRole.ROLE_ADMIN;

@RestController
@ConditionalOnProperty(prefix = "ru.comgrid.http", name = "trace-enabled")
@SecurityRequirement(name = "bearerAuth")
public class HttpTraceController{
	private final TraceRepository<HttpTrace> httpTraceRepository;

	public HttpTraceController(TraceRepository<HttpTrace> httpTraceRepository){
		this.httpTraceRepository = httpTraceRepository;
	}

	@GetMapping("/httptrace")
	@Secured(ROLE_ADMIN)
	public HttpTraceDescriptor traces() {
		return new HttpTraceDescriptor(this.httpTraceRepository.findAll());
	}

	/**
	 * A description of an application's {@link TraceRepository} entries. Primarily intended for
	 * serialization to JSON.
	 */
	public record HttpTraceDescriptor(Collection<HttpTrace> traces){
		public Collection<HttpTrace> getTraces(){
			return this.traces;
		}
	}
}
