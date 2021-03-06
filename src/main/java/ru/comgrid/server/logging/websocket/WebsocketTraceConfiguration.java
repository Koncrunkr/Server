package ru.comgrid.server.logging.websocket;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.messaging.support.InterceptableChannel;
import ru.comgrid.server.logging.InMemoryTraceRepository;
import ru.comgrid.server.logging.TraceRepository;
import ru.comgrid.server.security.AppProperties;

@Configuration
@ConditionalOnProperty(prefix = "ru.comgrid.websocket", name = "trace-enabled")
public class WebsocketTraceConfiguration{
	@Bean
	@Description("Repository for storing all traces")
	public TraceRepository<WebsocketTrace> webSocketTraceTraceRepository(AppProperties appProperties){
		return new InMemoryTraceRepository<>(appProperties.getWebsocket().getTraceMaxCount());
	}

	@Bean
	@Description("Channel interceptor for WebSocket tracing")
	public WebsocketTraceChannelInterceptor websocketTraceChannelInterceptor(
		TraceRepository<WebsocketTrace> webSocketTraceRepository
	){
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
