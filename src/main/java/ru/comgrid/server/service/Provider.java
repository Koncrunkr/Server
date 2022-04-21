package ru.comgrid.server.service;

import ru.comgrid.server.security.user.info.GoogleOAuth2UserInfo;
import ru.comgrid.server.security.user.info.OAuth2UserInfo;
import ru.comgrid.server.security.user.info.VkOauth2UserInfo;

import java.util.Map;
import java.util.function.Function;

public enum Provider{
	google(GoogleOAuth2UserInfo::new),
	vk(VkOauth2UserInfo::new);

	private final Function<Map<String, Object>, OAuth2UserInfo> converter;

	Provider(Function<Map<String, Object>, OAuth2UserInfo> converter){
		this.converter = converter;
	}

	public OAuth2UserInfo convert(Map<String, Object> map){
		return converter.apply(map);
	}

}
