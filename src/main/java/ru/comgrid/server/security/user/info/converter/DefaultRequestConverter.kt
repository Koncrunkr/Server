package ru.comgrid.server.security.user.info.converter

import org.springframework.http.RequestEntity
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter
import org.springframework.stereotype.Service
import ru.comgrid.server.security.user.info.extractor.Extractor
import ru.comgrid.server.service.Provider
import java.util.*
import java.util.stream.Collectors

private val AUTH_PROVIDERS = Arrays
    .stream(Provider.values())
    .map { obj: Provider -> obj.toString() }
    .filter { s: String -> s != "vk" }
    .collect(Collectors.toUnmodifiableSet())

@Service
class DefaultRequestConverter :
    Extractor<RequestEntity<*>, Nothing> {
    private val defaultConverter = OAuth2UserRequestEntityConverter()

    override fun canProceed(registrationId: String): Boolean {
        return AUTH_PROVIDERS.contains(registrationId)
    }

    override fun extract(o: Nothing?, userRequest: OAuth2UserRequest): RequestEntity<*> =
        defaultConverter.convert(userRequest)

}