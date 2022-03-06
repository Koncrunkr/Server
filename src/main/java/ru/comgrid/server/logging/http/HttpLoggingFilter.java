package ru.comgrid.server.logging.http;

import io.micrometer.core.instrument.util.IOUtils;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class HttpLoggingFilter extends OncePerRequestFilter{

	private final HttpTraceRepository httpTraceRepository;

	public HttpLoggingFilter(HttpTraceRepository httpTraceRepository){
		this.httpTraceRepository = httpTraceRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
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
		finally {
			Map<String, List<String>> headers = extractHeaders(responseWrapper);
			headers.put("body", List.of(IOUtils.toString(responseWrapper.getContentInputStream(), UTF_8)));

			Principal principal = request.getUserPrincipal();
			HttpTrace httpTrace = new HttpTrace(
				new HttpTrace.Request(
					requestWrapper.getMethod(),
					URI.create(requestWrapper.getRequestURI()),
					new ServletServerHttpRequest(requestWrapper).getHeaders(),
					requestWrapper.getRemoteAddr()
				),
				new HttpTrace.Response(status, headers),
				Instant.now(),
				new HttpTrace.Principal(principal == null ? null : principal.getName()),
				new HttpTrace.Session(request.getSession().getId()),
				System.nanoTime() - startTime
			);
			httpTraceRepository.add(httpTrace);
			responseWrapper.copyBodyToResponse();
		}
	}

	private static Map<String, List<String>> extractHeaders(HttpServletResponse response) {
		Map<String, List<String>> headers = new LinkedHashMap<>();
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
			Pattern.compile(".*\\.js")
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










