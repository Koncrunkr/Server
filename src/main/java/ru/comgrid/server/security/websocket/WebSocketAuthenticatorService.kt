package ru.comgrid.server.security.websocket

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import ru.comgrid.server.security.token.TokenProvider
import ru.comgrid.server.security.user.PersonDetailsService

@Component
class WebSocketAuthenticatorService(
    @param:Autowired private val tokenProvider: TokenProvider,
    @param:Autowired private val userDetailsService: PersonDetailsService,
) {

    fun getAuthenticationOrFail(jwtBearer: String?): UsernamePasswordAuthenticationToken {
        val jwt = getJwtToken(jwtBearer)
        val userId = tokenProvider.getUserIdFromToken(jwt)
        val userDetails = userDetailsService.loadUserByUsername(userId)
        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }

    fun getJwtToken(jwtBearer: String?): String {
        if(jwtBearer.isNullOrBlank() || !jwtBearer.startsWith("Bearer ")){
            throw InsufficientAuthenticationException("user.not-authorized");
        }

        val jwt = jwtBearer.substring(7)
        if(!tokenProvider.validateToken(jwt))
            throw InsufficientAuthenticationException("user.bad-token");
        return jwt
    }
}