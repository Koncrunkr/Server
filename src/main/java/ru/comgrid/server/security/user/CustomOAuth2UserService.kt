package ru.comgrid.server.security.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2AuthorizationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.UnknownContentTypeException
import ru.comgrid.server.model.Person
import ru.comgrid.server.repository.PersonRepository
import ru.comgrid.server.security.exception.OAuth2AuthenticationProcessingException
import ru.comgrid.server.security.exception.UserInfoAuthenticationException
import ru.comgrid.server.security.user.info.OAuth2UserInfo
import ru.comgrid.server.security.user.info.OAuth2UserInfoFactory.getOAuth2UserInfo
import ru.comgrid.server.security.user.info.UserPrincipal
import ru.comgrid.server.security.user.info.converter.RequestConverter
import ru.comgrid.server.security.user.info.extractor.UserInfoExtractorService

@Service
class CustomOAuth2UserService(
    @param:Autowired private val personRepository: PersonRepository,
    @param:Autowired private val userInfoExtractorService: UserInfoExtractorService,
    @param:Autowired private val requestConverter: RequestConverter,
) : DefaultOAuth2UserService() {

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val user = defaultLoadUser(userRequest)
        return try {
            processOAuth2User(userRequest, user)
        } catch (e: OAuth2AuthenticationException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @Throws(OAuth2AuthenticationException::class)
    fun defaultLoadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val userNameAttributeName = getUserNameAttributeName(userRequest)
        val request = this.requestConverter.convert(userRequest)
        val userAttributes = getResponse(userRequest, request)
        val authorities: MutableSet<GrantedAuthority> = LinkedHashSet()
        authorities.add(OAuth2UserAuthority(userAttributes))
        val token = userRequest.accessToken
        for (authority in token.scopes) {
            authorities.add(SimpleGrantedAuthority("SCOPE_$authority"))
        }
        return DefaultOAuth2User(authorities, userAttributes, userNameAttributeName)
    }

    @Throws(OAuth2AuthenticationException::class)
    private fun processOAuth2User(oAuth2UserRequest: OAuth2UserRequest, oAuth2User: OAuth2User): OAuth2User {
        val oAuth2UserInfo = getOAuth2UserInfo(
            oAuth2UserRequest.clientRegistration.registrationId,
            oAuth2User.attributes
        )

        if (oAuth2UserInfo.email.isNullOrBlank()) {
            throw OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider")
        }
        val userOptional = personRepository.findById(oAuth2UserInfo.id).orElse(null)

        val person: Person = if (userOptional != null) {
            if (userOptional.provider != Provider.valueOf(oAuth2UserRequest.clientRegistration.registrationId)) {
                throw OAuth2AuthenticationProcessingException(
                    "Looks like you're signed up with " +
                            userOptional.provider + " account. Please use your " + userOptional.provider +
                            " account to login."
                )
            }
            userOptional
        } else {
            registerNewUser(oAuth2UserRequest, oAuth2UserInfo)
        }
        return UserPrincipal.create(person, oAuth2User.attributes)
    }

    private fun getUserNameAttributeName(userRequest: OAuth2UserRequest): String {
        val client = userRequest.clientRegistration
        client.providerDetails.userInfoEndpoint.uri
        if (client.providerDetails.userInfoEndpoint.uri.isNullOrBlank()) {
            val oauth2Error = OAuth2Error(MISSING_USER_INFO_URI_ERROR_CODE,
                "Missing required UserInfo Uri in UserInfoEndpoint for Client Registration: "
                        + client.registrationId,
                null)
            throw OAuth2AuthenticationException(oauth2Error, oauth2Error.toString())
        }
        val userNameAttributeName = client.providerDetails.userInfoEndpoint.userNameAttributeName
        if (userNameAttributeName.isNullOrBlank()) {
            val oauth2Error = OAuth2Error(MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE,
                "Missing required \"user name\" attribute name in UserInfoEndpoint for Client Registration: "
                        + client.registrationId,
                null)
            throw OAuth2AuthenticationException(oauth2Error, oauth2Error.toString())
        }
        return userNameAttributeName
    }

    fun getResponse(userRequest: OAuth2UserRequest, request: RequestEntity<*>): Map<String, Any> {
        return try {
            val response: ResponseEntity<Map<String, Any>> =
                RestTemplate().exchange(request, Any::class.java) as ResponseEntity<Map<String, Any>>

            userInfoExtractorService.extract(response, userRequest)
        } catch (ex: OAuth2AuthorizationException) {
            val userInfoEndpoint = userRequest.clientRegistration.providerDetails.userInfoEndpoint.uri
            throw UserInfoAuthenticationException.of(ex, userInfoEndpoint)
        } catch (ex: UnknownContentTypeException) {
            val userInfoEndpoint = userRequest.clientRegistration.providerDetails.userInfoEndpoint.uri
            val registrationId = userRequest.clientRegistration.registrationId
            throw UserInfoAuthenticationException.of(ex, userInfoEndpoint, registrationId)
        } catch (ex: RestClientException) {
            throw UserInfoAuthenticationException.of(ex)
        }
    }

    private fun registerNewUser(oAuth2UserRequest: OAuth2UserRequest, oAuth2UserInfo: OAuth2UserInfo): Person {
        val person = Person(
            oAuth2UserInfo.id,
            oAuth2UserInfo.name,
            oAuth2UserInfo.email,
            oAuth2UserInfo.imageUrl,
            Provider.valueOf(oAuth2UserRequest.clientRegistration.registrationId)
        )
        return personRepository.save(person)
    }

    companion object {
        private const val MISSING_USER_INFO_URI_ERROR_CODE = "missing_user_info_uri"
        private const val MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE = "missing_user_name_attribute"
    }
}