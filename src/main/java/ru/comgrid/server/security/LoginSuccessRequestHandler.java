package ru.comgrid.server.security;

import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import ru.comgrid.server.model.Person;
import ru.comgrid.server.repository.PersonRepository;

import java.math.BigDecimal;
import java.util.Map;

class LoginSuccessRequestHandler extends OidcUserService{
    private final PersonRepository personRepository;

    private static final String idKey = "sub";

    public LoginSuccessRequestHandler(PersonRepository personRepository){
        this.personRepository = personRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException{
        var user = new CustomOidcUser(super.loadUser(userRequest));

        Map<String, Object> infoAboutUser = userRequest.getIdToken().getClaims();

        BigDecimal id = new BigDecimal(user.getName());
        if (personRepository.existsById(id)){
            return user;
        }

        Person person = new Person(
            id,
            (String) infoAboutUser.get("given_name"),
            (String) infoAboutUser.get("email"),
            (String) infoAboutUser.get("picture")
        );
        person.setNew(true);

        personRepository.save(person);

        return user;
    }
}
