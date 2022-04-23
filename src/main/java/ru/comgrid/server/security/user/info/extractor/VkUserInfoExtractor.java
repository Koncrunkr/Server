package ru.comgrid.server.security.user.info.extractor;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Service;
import ru.comgrid.server.service.Provider;

import java.util.List;
import java.util.Map;

@Service
public class VkUserInfoExtractor implements Extractor<Map<String, Object>, Map<String, Object>>{
	private static final String vk = Provider.vk.toString();

	@Override
	public boolean canProceed(String registrationId){
		return vk.equals(registrationId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> extract(Map<String, Object> map, OAuth2UserRequest userRequest){
		var attributes = ((List<Map<String, Object>>) map.get("response")).get(0);
		attributes.put("email", userRequest.getAdditionalParameters().get("email"));
		return attributes;
	}
}
