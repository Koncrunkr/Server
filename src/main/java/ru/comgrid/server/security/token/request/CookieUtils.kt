package ru.comgrid.server.security.token.request

import org.springframework.util.SerializationUtils
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

fun getCookie(request: HttpServletRequest, name: String): Cookie? {
    return request.cookies?.find { it.name == name }
}

fun addCookie(response: HttpServletResponse, name: String?, value: String?, maxAge: Int) {
    val cookie = Cookie(name, value)
    cookie.path = "/"
    cookie.isHttpOnly = true
    cookie.maxAge = maxAge
    response.addCookie(cookie)
}

fun deleteCookie(request: HttpServletRequest, response: HttpServletResponse, name: String) {
    val cookies = request.cookies
    cookies
        ?.filter { it.name == name }
        ?.forEach { cookie ->
            cookie.value = ""
            cookie.path = "/"
            cookie.maxAge = 0
            response.addCookie(cookie)
        }
}

fun serialize(`object`: Any?): String {
    return Base64.getUrlEncoder()
        .encodeToString(SerializationUtils.serialize(`object`))
}

fun <T> deserialize(cookie: Cookie): T {
    return SerializationUtils.deserialize(
        Base64.getUrlDecoder().decode(cookie.value)) as T
}