package ru.comgrid.server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import ru.comgrid.server.repository.PersonRepository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSecurity
public class SecurityConfig
        extends WebSecurityConfigurerAdapter {

    private final PersonRepository personRepository;
    private final JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();

    public SecurityConfig(
        @Autowired PersonRepository personRepository,
        @Autowired JdbcTemplate jdbcTemplate
    ){
        this.personRepository = personRepository;
        jdbcTokenRepository.setJdbcTemplate(jdbcTemplate);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors()
                .and()
            .csrf()
                .disable()
            .authorizeRequests()
                .antMatchers("/", "/error", "/login*", "/oauth/**", "/user/login", "/image/upload")
                .permitAll()
            .anyRequest()
                .authenticated()
                .and()
            .oauth2Login()
                .and()
            .logout()
                .logoutSuccessUrl("https://comgrid.ru/")
                .permitAll()
//                .logoutSuccessHandler((request, response, authentication) -> {
//                    jdbcTokenRepository.remove(((CustomUserDetails) authentication.getPrincipal()).getUsername());
//                })
                .and()
            .rememberMe()
                .alwaysRemember(true)
                .userDetailsService(CustomUserDetails::new)
                .tokenRepository(jdbcTokenRepository)
                .useSecureCookie(true)
                .and();
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> simpleCorsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("https://comgrid.ru"));
        config.setAllowedMethods(List.of("GET", "HEAD", "POST", "PUT", "DELETE", "PATCH"));
        config.setAllowedHeaders(List.of("Content-Types", "Content-Type", "authorization", "x-auth-token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    public HttpTraceRepository httpTraceRepository(){
        return new InMemoryHttpTraceRepository();
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> loginService(){
        return new LoginSuccessRequestHandler(personRepository);
    }
}