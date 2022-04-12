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

//    @Bean
//    public static ClientRegistration getVk() {
//        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId("vk");
////            .getBuilder("vk", ClientAuthenticationMethod.POST, "{baseUrl}/{action}/oauth2/code/{registrationId}");
//        builder.scope("3");
//        builder.clientAuthenticationMethod(ClientAuthenticationMethod.POST);
//        builder.authorizationUri("https://oauth.vk.com/authorize?v=5.95");
//        builder.tokenUri("https://oauth.vk.com/access_token");
//        builder.userInfoUri("https://api.vk.com/method/users.get?{user_id}&v=5.95&fields=photo_id,verified,sex,bdate,city,country,photo_max,home_town,has_photo&display=popup&lang=ru&access_token=xxxxx");
//        builder.clientName("vkontakte");
//        builder.redirectUri("{baseUrl}/oauth2/callback/{registrationId}");
//        builder.clientId("8135349");
//        builder.clientSecret("Lss2aX961WluwqP3qjWJ");
//        builder.userNameAttributeName("user_id");
//        builder.registrationId("vk");
//        return builder.build();
//    }
}