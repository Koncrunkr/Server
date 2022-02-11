package ru.comgrid.server.security;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import ru.comgrid.server.model.Person;
import ru.comgrid.server.repository.PersonRepository;

import java.math.BigInteger;
import java.util.Map;

public class LoginSuccessRequestHandler extends OidcUserService{

    private final PersonRepository personRepository;

    private static final String idKey = "sub";

    public LoginSuccessRequestHandler(PersonRepository personRepository){
        this.personRepository = personRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException{
        var user = super.loadUser(userRequest);
        Map<String, Object> infoAboutUser = userRequest.getIdToken().getClaims();

        BigInteger id = new BigInteger((String) infoAboutUser.get(idKey));
        if(personRepository.existsById(id)){
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
