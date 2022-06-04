package ru.comgrid.server.security.websocket;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.*;
import ru.comgrid.server.exception.ExceptionMessageConverter;
import ru.comgrid.server.security.AppProperties;
import ru.comgrid.server.security.websocket.destination.UserSubscriptionInterceptor;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebsocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer implements WebSocketConfigurer{
    private final UserSubscriptionInterceptor userSubscriptionInterceptor;
    private final AuthChannelInterceptor authInterceptor;
    private final AppProperties appProperties;

    public WebsocketConfig(
        @Autowired UserSubscriptionInterceptor userSubscriptionInterceptor,
        @Autowired AuthChannelInterceptor authInterceptor,
        @Autowired AppProperties appProperties
    ){
        this.userSubscriptionInterceptor = userSubscriptionInterceptor;
        this.authInterceptor = authInterceptor;
        this.appProperties = appProperties;
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters){
        messageConverters.add(new ExceptionMessageConverter());
        return true;
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages){
        messages
            .simpDestMatchers("/connection/*").authenticated()
            .simpSubscribeDestMatchers("/connection/*").authenticated();
    }

    @Override
    @Order(Ordered.HIGHEST_PRECEDENCE)
    protected void customizeClientInboundChannel(ChannelRegistration registration){
        registration.interceptors(authInterceptor);
        registration.interceptors(userSubscriptionInterceptor);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.setApplicationDestinationPrefixes("/connection");
        registry.setUserDestinationPrefix("/queue");
        var rabbitConfig = appProperties.getWebsocket().getRabbitMq();
        registry.enableStompBrokerRelay()
            .setRelayHost(rabbitConfig.getRelayHost())
            .setRelayPort(rabbitConfig.getRelayPort())
            .setSystemLogin(rabbitConfig.getSystemLogin())
            .setSystemPasscode(rabbitConfig.getSystemPassword())
            .setClientLogin(rabbitConfig.getClientLogin())
            .setClientPasscode(rabbitConfig.getClientPassword());
    }


    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry){
        registry.addEndpoint("/websocket").setAllowedOrigins("https://comgrid.ru", "https://comgrid.ru:444", "http://localhost:3000", "http://localhost:8080").withSockJS();
        registry.addEndpoint("/websocket").setAllowedOrigins("https://comgrid.ru", "https://comgrid.ru:444", "http://localhost:3000", "http://localhost:8080");
    }

    @Override
    protected boolean sameOriginDisabled(){
        return true;
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration){
        registration
            .addDecoratorFactory(DelegatingWebsocketHandler::new)
            .setMessageSizeLimit(appProperties.getWebsocket().getMaxMessageSizeBytes());
    }

    @Override
    public void registerWebSocketHandlers(@NotNull WebSocketHandlerRegistry registry){
    }
}

