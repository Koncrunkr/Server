package ru.comgrid.server.logging.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.messaging.support.InterceptableChannel;

@Configuration
@ConditionalOnProperty(prefix = "ru.comgrid.websocket.trace", name = "enabled")
public class WebSocketTraceConfiguration{

	@Bean
	public InMemoryWebSocketTraceRepository webSocketTraceRepository(@Value("${ru.comgrid.websocket.trace.max-count}") int capacity){
		return new InMemoryWebSocketTraceRepository(capacity);
	}

	@Bean
	@Description("Channel interceptor for WebSocket tracing")
	public WebsocketTraceChannelInterceptor websocketTraceChannelInterceptor(WebSocketTraceRepository webSocketTraceRepository){
		System.out.println("sdfsdf1");
		return new WebsocketTraceChannelInterceptor(webSocketTraceRepository);
	}

	@Bean
	@Description("CLR that adds the required channel interceptors for tracing")
	public CommandLineRunner addTraceInterceptor(
		WebsocketTraceChannelInterceptor webSocketTraceChannelInterceptor,
		InterceptableChannel clientInboundChannel,
		InterceptableChannel clientOutboundChannel
	) {
		return (args) -> {
			clientInboundChannel.addInterceptor(webSocketTraceChannelInterceptor);
			clientOutboundChannel.addInterceptor(webSocketTraceChannelInterceptor);
		};
	}

}
