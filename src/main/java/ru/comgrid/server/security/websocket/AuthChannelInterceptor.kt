package ru.comgrid.server.security.websocket

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component


private const val AUTHORIZATION_HEADER = "Authorization"
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class AuthChannelInterceptor(
    @param:Autowired private val webSocketAuthenticatorService: WebSocketAuthenticatorService,
) : ChannelInterceptor{
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)!!

        if (StompCommand.CONNECT == accessor.command ||
            StompCommand.SEND == accessor.command ||
            StompCommand.SUBSCRIBE == accessor.command) {
            val jwtBearer = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER)
            val user: UsernamePasswordAuthenticationToken =
                webSocketAuthenticatorService.getAuthenticationOrFail(jwtBearer)
            accessor.user = user
        }
        return message
    }
}