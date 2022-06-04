package ru.comgrid.server.security.user.info

import ru.comgrid.server.security.exception.OAuth2AuthenticationProcessingException
import ru.comgrid.server.security.user.Provider

object OAuth2UserInfoFactory {
    private val AUTH_PROVIDERS = listOf(*Provider.values())
    fun getOAuth2UserInfo(registrationId: String, attributes: Map<String, Any?>): OAuth2UserInfo {
        return AUTH_PROVIDERS.stream()
            .filter { authProvider: Provider -> authProvider.toString() == registrationId }
            .findFirst()
            .orElseThrow { OAuth2AuthenticationProcessingException("We don't support $registrationId yet") }
            .convert(attributes)
    }
}