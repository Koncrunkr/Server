package ru.comgrid.server.logging.http;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import ru.comgrid.server.logging.TraceRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;

@Component
@Order(100)
@ConditionalOnProperty(prefix = "ru.comgrid.http", name = "trace-enabled")
public class HttpLoggingFilter extends OncePerRequestFilter{
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private final TraceRepository<HttpTrace> httpTraceRepository;
	private static final String json = "application/json";

	public HttpLoggingFilter(TraceRepository<HttpTrace> httpTraceRepository){
		this.httpTraceRepository = httpTraceRepository;
	}

	@Override
	protected void doFilterInternal(
		@NotNull HttpServletRequest request,
		@NotNull HttpServletResponse response,
		@NotNull FilterChain filterChain
	) throws ServletException, IOException{
		if(IgnoredRequests.isIgnored(request.getRequestURI())){
			filterChain.doFilter(request, response);
			return;
		}

		long startTime = System.nanoTime();
		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

		int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
		try {
			filterChain.doFilter(requestWrapper, responseWrapper);
			status = response.getStatus();
		}
		finally{
			HttpHeaders headers = extractHeaders(responseWrapper);
			Principal principal = request.getUserPrincipal();

			JsonNode requestBody;
			try{
				requestBody = objectMapper.readTree(requestWrapper.getContentAsByteArray());
			}catch(JsonParseException e){
				requestBody = objectMapper.nullNode();
			}
			JsonNode responseBody;
			try{
				responseBody = objectMapper.readTree(responseWrapper.getContentAsByteArray());
			}catch(JsonParseException e){
				responseBody = objectMapper.nullNode();
			}
			HttpTrace httpTrace = new HttpTrace(
				new HttpTrace.Request(
					requestWrapper.getMethod(),
					URI.create(requestWrapper.getRequestURI()),
					new ServletServerHttpRequest(requestWrapper).getHeaders(),
					requestBody,
					requestWrapper.getRemoteAddr()
				),
				new HttpTrace.Response(
					status,
					responseBody,
					headers
				),
				principal,
				Instant.now(),
				System.nanoTime() - startTime
			);
			httpTraceRepository.add(httpTrace);
			responseWrapper.copyBodyToResponse();
		}
	}

	private static HttpHeaders extractHeaders(HttpServletResponse response) {
		HttpHeaders headers = new HttpHeaders();
		for (String name : response.getHeaderNames()) {
			headers.put(name, new ArrayList<>(response.getHeaders(name)));
		}
		return headers;
	}


	private static class IgnoredRequests{
		private static final Set<String> ignoredRequests = Set.of(
			"/httptrace",
			"/example_messaging",
			"/example_post",
			"/image/upload"
		);
		private static final Set<Pattern> ignoredPatterns = Set.of(
			Pattern.compile(".*\\.css"),
			Pattern.compile(".*\\.js"),
			Pattern.compile(".*/images/.*")
		);
		static boolean isIgnored(String request){
			if(ignoredRequests.contains(request))
				return true;
			for(Pattern ignoredPattern : ignoredPatterns){
				if(ignoredPattern.matcher(request).find())
					return true;
			}
			return false;
		}
	}
}










