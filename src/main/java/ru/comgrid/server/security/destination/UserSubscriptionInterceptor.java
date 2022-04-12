package ru.comgrid.server.security.destination;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import ru.comgrid.server.api.user.UserHelp;
import ru.comgrid.server.exception.IllegalAccessException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserSubscriptionInterceptor implements ChannelInterceptor{

    private final Map<String, IndividualDestinationInterceptor> individualDestinationInterceptors;

    public UserSubscriptionInterceptor(List<IndividualDestinationInterceptor> individualDestinationInterceptors){
        this.individualDestinationInterceptors =
            individualDestinationInterceptors
                .stream()
                .collect(
                    HashMap::new,
                    (map, element) -> map.put(element.destination(), element),
                    HashMap::putAll
                );
    }

    @Override
    public Message<?> preSend(
        @NonNull Message<?> message,
        @NonNull MessageChannel channel
    ){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        if(StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())){
            verifyAccessGranted(headerAccessor);
        }
        return message;
    }

    private static final int DESTINATION_INDEX = 2;
    private static final int DESTINATION_ID_INDEX = 3;
    /**
     * fullDestination looks somewhat like this:
     * /connection/{destination}/{destinationId}
     * so if we split it, destination would be on 2nd index, and destinationId on 4th
     */
    private void verifyAccessGranted(StompHeaderAccessor headerAccessor){
        String[] fullDestination = extractFullDestination(headerAccessor);
        String destinationId = fullDestination[DESTINATION_ID_INDEX];
        String destination = fullDestination[DESTINATION_INDEX];

        Authentication user = (Authentication) headerAccessor.getUser();
        assert user != null;
        BigDecimal userId = UserHelp.extractId((UserDetails) user.getPrincipal());

        if(!individualDestinationInterceptors.get(destination).hasAccess(userId, destinationId)){
            throw new IllegalAccessException("destination." + destination);
        }
    }

    private String[] extractFullDestination(StompHeaderAccessor headerAccessor){
        List<String> destinations = headerAccessor.getNativeHeader("destination");
        assert destinations != null;
        if(destinations.size() != 1)
            throw new IllegalArgumentException("Multiple destinations are not supported");
        String[] fullDestination = destinations.get(0).split("/");
        if(fullDestination.length != 4)
            throw new IllegalArgumentException("Destination is not correct: " + destinations.get(0));
        return fullDestination;
    }
}
