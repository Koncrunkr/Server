package ru.comgrid.server.security.user.info.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.web.util.UriComponentsBuilder

class VkUserInfoRequestConverter : Converter<OAuth2UserRequest, RequestEntity<*>> {
    private val defaultConverter = OAuth2UserRequestEntityConverter()

    override fun convert(userRequest: OAuth2UserRequest): RequestEntity<*> {
        val client = userRequest.clientRegistration
        if(client.clientId != "vk")
            return defaultConverter.convert(userRequest)
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.contentType = DEFAULT_CONTENT_TYPE

        val uri = UriComponentsBuilder
            .fromUriString(client.providerDetails.userInfoEndpoint.uri)
            .queryParam(OAuth2ParameterNames.ACCESS_TOKEN, userRequest.accessToken.tokenValue)
            .queryParam("user_ids", userRequest.additionalParameters["user_id"].toString())
            .queryParam("fields", DEFAULT_FIELDS)
            .queryParam("v", "5.131")
            .build().toUri()

        return RequestEntity<Any>(headers, HttpMethod.POST, uri)
    }

    companion object {
        private val DEFAULT_CONTENT_TYPE = MediaType.APPLICATION_JSON
        private val DEFAULT_FIELDS = listOf("photo_max_orig")
    }
}