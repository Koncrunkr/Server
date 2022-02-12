package ru.comgrid.server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import ru.comgrid.server.security.destination.UserSubscriptionInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer{


    private final UserSubscriptionInterceptor userSubscriptionInterceptor;

    public WebsocketConfig(@Autowired UserSubscriptionInterceptor userSubscriptionInterceptor){this.userSubscriptionInterceptor = userSubscriptionInterceptor;}

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages){
        messages
            .simpDestMatchers("/app/*").authenticated()
            .simpSubscribeDestMatchers("/app/*").authenticated();
    }

    @Override
    protected void customizeClientInboundChannel(ChannelRegistration registration){
        registration.interceptors(userSubscriptionInterceptor);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry){
        registry.addEndpoint("/messaging").withSockJS();
    }

    @Override
    protected boolean sameOriginDisabled(){
        return true;
    }
}

