package ru.comgrid.server.security.websocket;

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
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import ru.comgrid.server.exception.ExceptionMessageConverter;
import ru.comgrid.server.security.destination.UserSubscriptionInterceptor;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebsocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer{
    private final UserSubscriptionInterceptor userSubscriptionInterceptor;
    private final AuthChannelInterceptor authInterceptor;

    public WebsocketConfig(
        @Autowired UserSubscriptionInterceptor userSubscriptionInterceptor,
        @Autowired AuthChannelInterceptor authInterceptor
    ){
        this.userSubscriptionInterceptor = userSubscriptionInterceptor;
        this.authInterceptor = authInterceptor;
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
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry){
        registry.addEndpoint("/websocket").setAllowedOrigins("https://comgrid.ru").withSockJS();
        registry.addEndpoint("/websocket").setAllowedOrigins("https://comgrid.ru");
    }

    @Override
    protected boolean sameOriginDisabled(){
        return true;
    }

}

