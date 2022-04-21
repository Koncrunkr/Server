package ru.comgrid.server.security.user.info;

import java.math.BigDecimal;
import java.util.Map;

public abstract class OAuth2UserInfo{
	protected Map<String, Object> attributes;

	public OAuth2UserInfo(Map<String, Object> attributes){
		this.attributes = attributes;
	}

	public Map<String, Object> getAttributes(){
		return attributes;
	}

	public abstract BigDecimal getId();

	public abstract String getName();

	public abstract String getEmail();

	public abstract String getImageUrl();
}
