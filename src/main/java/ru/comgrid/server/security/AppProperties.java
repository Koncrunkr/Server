package ru.comgrid.server.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;

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
	}

	@Getter
	@Setter
	public static class Websocket{
		private int maxMessageSizeBytes;
		private boolean traceEnabled;
		private int traceMaxCount;
		private RabbitMqConfig rabbitMq = new RabbitMqConfig();

		@Getter
		@Setter
		public static class RabbitMqConfig{
			private String relayHost;
			private int relayPort;
			private String systemLogin;
			private String systemPassword;
			private String clientLogin;
			private String clientPassword;
		}
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
		private int searchChunkSize;
	}
}
