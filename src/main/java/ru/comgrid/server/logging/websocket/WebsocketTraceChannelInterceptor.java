package ru.comgrid.server.logging.websocket;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WebsocketTraceChannelInterceptor implements ChannelInterceptor{

	private final WebSocketTraceRepository traceRepository;

	public WebsocketTraceChannelInterceptor(WebSocketTraceRepository traceRepository){
		this.traceRepository = traceRepository;
	}

	@Override
	public void afterSendCompletion(
		@NonNull Message<?> message,
		@NonNull MessageChannel channel,
		boolean sent, Exception ex
	){
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

		// Don't trace non-STOMP messages (like heartbeats)
		if(headerAccessor.getCommand() == null)
			return;

		final var webSocketTrace = new WebSocketTrace(
			headerAccessor.getSessionId(),
			headerAccessor.getCommand().name(),
			getNativeHeaders(headerAccessor),
			new String((byte[]) message.getPayload())
		);

		traceRepository.add(webSocketTrace);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getNativeHeaders(StompHeaderAccessor headerAccessor){
		Map<String, Object> nativeHeaders = (Map<String, Object>) headerAccessor.getHeader(NativeMessageHeaderAccessor.NATIVE_HEADERS);

		if(nativeHeaders == null)
			return Collections.emptyMap();

		Map<String, Object> traceHeaders = new LinkedHashMap<String, Object>();

		for(String header : nativeHeaders.keySet()) {
			List<String> headerValue = (List<String>) nativeHeaders.get(header);
			Object value = headerValue;

			if(headerValue.size() == 1) {
				value = headerValue.get(0);
			} else if(headerValue.isEmpty()) {
				value = "";
			}

			traceHeaders.put(header, value);
		}

		return traceHeaders;
	}
}
