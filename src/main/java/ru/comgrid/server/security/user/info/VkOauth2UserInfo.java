package ru.comgrid.server.security.user.info;

import java.math.BigDecimal;
import java.util.Map;

public class VkOauth2UserInfo extends OAuth2UserInfo{
	public VkOauth2UserInfo(Map<String, Object> attributes){
		super(attributes);
	}

	@Override
	public BigDecimal getId(){
		return new BigDecimal(String.valueOf(attributes.get("id")));
	}

	@Override
	public String getName(){
		return getAttributes().get("first_name").toString() + getAttributes().get("last_name").toString();
	}

	@Override
	public String getEmail(){
		return String.valueOf(getAttributes().get("email"));
	}

	@Override
	public String getImageUrl(){
		return String.valueOf(getAttributes().get("photo_max_orig"));
	}
}
