package ru.comgrid.server.security

import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val logger = LoggerFactory.getLogger(OnUnauthorizedRequestEntryPoint::class.java)

class OnUnauthorizedRequestEntryPoint : AuthenticationEntryPoint {
    @Throws(IOException::class, ServletException::class)
    override fun commence(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        e: AuthenticationException,
    ) {
        logger.error("Responding with unauthorized error. Message - {}", e.message)
        e.printStackTrace()
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
            e.localizedMessage)
    }
}