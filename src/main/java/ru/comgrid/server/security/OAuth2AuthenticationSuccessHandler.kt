package ru.comgrid.server.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import ru.comgrid.server.security.exception.BadRequestException
import ru.comgrid.server.security.token.TokenProvider
import ru.comgrid.server.security.token.request.REDIRECT_URI_PARAM_COOKIE_NAME
import ru.comgrid.server.security.token.request.getCookie
import java.io.IOException
import java.net.URI
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class OAuth2AuthenticationSuccessHandler(
    @param:Autowired private val appProperties: AppProperties,
    @param:Autowired private val tokenProvider: TokenProvider,
) : SimpleUrlAuthenticationSuccessHandler() {
    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        super.onAuthenticationSuccess(request, response, authentication)
    }

    override fun determineTargetUrl(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ): String {
        val redirectUrl = getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)?.value
        if (redirectUrl != null && !isAuthorizedRedirectUri(redirectUrl)) {
            throw BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication")
        }
        val targetUrl = redirectUrl ?: defaultTargetUrl
        val token = tokenProvider.createToken(authentication)
        return UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam("token", token)
            .build().toUriString()
    }

    private fun isAuthorizedRedirectUri(uri: String): Boolean {
        val clientRedirectUri = URI.create(uri)
        return appProperties.auth.authorizedRedirectUris
            .any { authorizedRedirectUri: String ->
                // Only validate host and port. Let the clients use different paths if they want to
                val authorizedURI = URI.create(authorizedRedirectUri)
                (authorizedURI.host.equals(clientRedirectUri.host, ignoreCase = true)
                        && authorizedURI.port == clientRedirectUri.port)
            }
    }
}