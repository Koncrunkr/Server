package ru.comgrid.server.logging.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@ConditionalOnProperty(prefix = "ru.comgrid.http.trace", name = "enabled")
public class HttpTraceController{
	private final HttpTraceRepository httpTraceRepository;

	public HttpTraceController(@Autowired HttpTraceRepository httpTraceRepository){
		this.httpTraceRepository = httpTraceRepository;
	}

	@GetMapping("/httptrace")
	public HttpTraceDescriptor traces() {
		return new HttpTraceDescriptor(this.httpTraceRepository.findAll());
	}

	/**
	 * A description of an application's {@link HttpTrace} entries. Primarily intended for
	 * serialization to JSON.
	 */
	public record HttpTraceDescriptor(List<HttpTrace> traces){
		public List<HttpTrace> getTraces(){
			return this.traces;
		}
	}
}
