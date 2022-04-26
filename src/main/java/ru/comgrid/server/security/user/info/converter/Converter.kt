package ru.comgrid.server.security.user.info.converter

import org.springframework.http.RequestEntity
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest

interface Converter<T> {
    fun canProceed(registrationId: String): Boolean
    fun extract(userRequest: OAuth2UserRequest): T
}