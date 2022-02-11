package ru.comgrid.server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import ru.comgrid.server.repository.PersonRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig
        extends WebSecurityConfigurerAdapter {

    private final PersonRepository personRepository;

    public SecurityConfig(@Autowired PersonRepository personRepository){this.personRepository = personRepository;}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/", "/error", "/login*", "/oauth/**")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .oauth2Login();
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> loginService(){
        return new LoginSuccessRequestHandler(personRepository);
    }
}