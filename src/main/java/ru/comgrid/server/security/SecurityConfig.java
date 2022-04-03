package ru.comgrid.server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.util.Pair;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import ru.comgrid.server.repository.PersonRepository;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSecurity
public class SecurityConfig
        extends WebSecurityConfigurerAdapter {

    private final PersonRepository personRepository;

    public SecurityConfig(@Autowired PersonRepository personRepository){
        this.personRepository = personRepository;
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
                .logoutSuccessHandler((request, response, authentication) -> {
                    System.out.println(authentication.getPrincipal());
                    tokenRepository.remove(((CustomUserDetails) authentication.getPrincipal()).getUsername());
                })
                .and()
            .rememberMe()
                .alwaysRemember(true)
                .userDetailsService(CustomUserDetails::new)
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

    static final ConcurrentHashMap<String, Pair<String, Collection<? extends GrantedAuthority>>> tokenRepository =
        new ConcurrentHashMap<>();
}