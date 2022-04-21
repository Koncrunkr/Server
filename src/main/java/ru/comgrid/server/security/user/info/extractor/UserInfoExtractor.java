package ru.comgrid.server.security.user.info.extractor;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

import java.util.Map;

public interface UserInfoExtractor{
	boolean canProceed(String registrationId);

	Map<String, Object> extract(Map<String, Object> o, OAuth2UserRequest userRequest);
}
