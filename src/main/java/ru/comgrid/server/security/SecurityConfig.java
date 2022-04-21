package ru.comgrid.server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import ru.comgrid.server.security.token.TokenAuthenticationFilter;
import ru.comgrid.server.security.token.TokenProvider;
import ru.comgrid.server.security.token.request.HttpCookieOAuth2AuthorizationRequestRepository;
import ru.comgrid.server.security.token.request.VkAccessTokenResponseClient;
import ru.comgrid.server.security.user.CustomOAuth2UserService;
import ru.comgrid.server.security.user.PersonDetailsService;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	private final CustomOAuth2UserService oAuth2UserService;
	private final OAuth2AuthenticationSuccessHandler successHandler;
	private final OAuth2AuthenticationFailureHandler failureHandler;
	private final HttpCookieOAuth2AuthorizationRequestRepository cookieRepository;
	private final TokenAuthenticationFilter tokenAuthenticationFilter;
	private final VkAccessTokenResponseClient vkAccessTokenResponseClient;

	public SecurityConfig(
		@Autowired PersonDetailsService userDetailsService,
		@Autowired CustomOAuth2UserService oAuth2UserService,
		@Autowired OAuth2AuthenticationSuccessHandler successHandler,
		@Autowired OAuth2AuthenticationFailureHandler failureHandler,
		@Autowired HttpCookieOAuth2AuthorizationRequestRepository cookieRepository,
		@Autowired TokenProvider tokenProvider,
		@Autowired VkAccessTokenResponseClient vkAccessTokenResponseClient
	){
		this.oAuth2UserService = oAuth2UserService;
		this.successHandler = successHandler;
		this.failureHandler = failureHandler;
		this.cookieRepository = cookieRepository;
		this.vkAccessTokenResponseClient = vkAccessTokenResponseClient;
		this.tokenAuthenticationFilter = new TokenAuthenticationFilter(tokenProvider, userDetailsService);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http
			.cors()
				.and()
			.csrf()
				.disable()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
			.httpBasic()
				.disable()
			.exceptionHandling()
				.authenticationEntryPoint(new OnUnauthorizedRequestEntryPoint())
				.and()
			.authorizeRequests()
				.antMatchers(
					"/",
					"/error",
					"/login/**",
					"/auth/**",
					"/oauth/**",
					"/oauth2/**",
					"/user/login",
					"/image/upload"
				)
				.permitAll()
			.anyRequest()
				.authenticated()
				.and()
			.oauth2Login()
				.authorizationEndpoint()
					.baseUri("/oauth2/authorize")
					.authorizationRequestRepository(cookieRepository)
					.and()
				.redirectionEndpoint()
					.baseUri("/oauth2/callback/*")
					.and()
				.userInfoEndpoint()
					.userService(oAuth2UserService)
					.and()
				.tokenEndpoint()
					.accessTokenResponseClient(vkAccessTokenResponseClient)
					.and()
				.successHandler(successHandler)
				.failureHandler(failureHandler)
				.and()
			.logout()
				.logoutSuccessUrl("https://comgrid.ru/")
				.permitAll()
				.and()
			.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

	}

    @Bean
    public FilterRegistrationBean<CorsFilter> simpleCorsFilter(){
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

	//    @Bean
	//    public OAuth2UserService<OidcUserRequest, OidcUser> loginService(){
	//        return new LoginSuccessRequestHandler(personRepository);
	//    }
}