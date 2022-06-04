package ru.comgrid.server.security.user.info.extractor;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Service;
import ru.comgrid.server.security.user.Provider;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DefaultUserInfoExtractor implements Extractor<Map<String, Object>, Map<String, Object>>{
	private static final Set<String> AUTH_PROVIDERS = Arrays
		.stream(Provider.values())
		.map(Enum::toString)
		.filter(s -> !s.equals("vk"))
		.collect(Collectors.toUnmodifiableSet());

	@Override
	public boolean canProceed(String registrationId){
		return AUTH_PROVIDERS.contains(registrationId);
	}

	@Override
	public Map<String, Object> extract(Map<String, Object> o, OAuth2UserRequest userRequest){
		return o;
	}
}
