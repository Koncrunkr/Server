package ru.comgrid.server.security.token

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import ru.comgrid.server.security.AppProperties
import ru.comgrid.server.security.user.info.UserPrincipal
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Service
class TokenProvider(
    @param:Autowired private val appProperties: AppProperties,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(TokenProvider::class.java)
    }

    private val tokenSecret: ByteArray = Decoders.BASE64.decode(appProperties.auth.tokenSecret)
    private val jwtParser: JwtParser = Jwts.parserBuilder()
        .setSigningKey(appProperties.auth.tokenSecret)
        .build()

    fun createToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserPrincipal
        val now = Date()
        val expiryDate = Date(now.time + appProperties.auth.tokenExpirationMsec)
        val key: Key = SecretKeySpec(tokenSecret, SignatureAlgorithm.HS512.jcaName)
        return Jwts.builder()
            .setSubject(userPrincipal.username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key)
            .compact()
    }

    fun getUserIdFromToken(token: String): String {
        val claims = jwtParser
            .parseClaimsJws(token)
            .body
        return claims.subject
    }

    fun validateToken(authToken: String): Boolean {
        try {
            jwtParser.parseClaimsJws(authToken)
            return true
        } catch (ex: SignatureException) {
            logger.error("Invalid JWT signature")
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token")
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token")
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty.")
        }
        return false
    }
}