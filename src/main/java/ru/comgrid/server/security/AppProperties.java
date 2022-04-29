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
	private final Websocket websocket = new Websocket();
	private final Http http = new Http();
	private final Table table = new Table();

	@Getter
	@Setter
	public static final class Auth{
		@NonNull
		private String vkAccessToken;
		@NonNull
		private String tokenSecret;
		private long tokenExpirationMsec;
		private List<String> authorizedRedirectUris = new ArrayList<>();
		private String adminKey;

		public String getAdminKey(){
			return Encoders.BASE64.encode(adminKey.getBytes(StandardCharsets.UTF_8));
		}

		public String getTokenSecret(){
			return Encoders.BASE64.encode(tokenSecret.getBytes(StandardCharsets.UTF_8));
		}
	}

	@Getter
	@Setter
	public static class Websocket{
		private int maxMessageSizeBytes;
		private boolean traceEnabled;
		private int traceMaxCount;
	}

	@Setter
	@Getter
	public static class Http{
		private boolean traceEnabled;
		private int traceMaxCount;
	}

	@Setter
	@Getter
	public static class Images{
		private String fileRoute;
		private List<String> allowedExtensions;
	}

	@Setter
	@Getter
	public static class Table{
		private int maxTableSize;
	}
}
