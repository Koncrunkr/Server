package ru.comgrid.server.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import ru.comgrid.server.security.token.request.HttpCookieOAuth2AuthorizationRequestRepository
import ru.comgrid.server.security.token.request.REDIRECT_URI_PARAM_COOKIE_NAME
import ru.comgrid.server.security.token.request.getCookie
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class OAuth2AuthenticationFailureHandler(
    @param:Autowired private val cookieRepository: HttpCookieOAuth2AuthorizationRequestRepository,
) : SimpleUrlAuthenticationFailureHandler() {

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException,
    ) {
        var targetUrl = getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)?.value ?: "/"

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam("error", exception.localizedMessage)
            .build().toUriString()
        cookieRepository.removeAuthorizationRequestCookies(request, response)
        redirectStrategy.sendRedirect(request, response, targetUrl)
    }
}