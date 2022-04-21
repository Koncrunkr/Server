package ru.comgrid.server.security;

import io.jsonwebtoken.io.Encoders;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Getter
@ConfigurationProperties(prefix = "ru.comgrid")
public class AppProperties{
	private final Auth auth = new Auth();

	@Getter
	@Setter
	public static final class Auth{
		@NonNull
		private String vkAccessToken;
		@NonNull
		private String tokenSecret;
		private long tokenExpirationMsec;
		private List<String> authorizedRedirectUris = new ArrayList<>();

		public String getTokenSecret(){
			return Encoders.BASE64.encode(tokenSecret.getBytes(StandardCharsets.UTF_8));
		}
	}
}
