package ru.comgrid.server.security.user.info;

import java.math.BigDecimal;
import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo{

	public GoogleOAuth2UserInfo(Map<String, Object> attributes){
		super(attributes);
	}

	@Override
	public String getName(){
		return (String) attributes.get("name");
	}

	@Override
	public String getImageUrl(){
		return (String) attributes.get("picture");
	}

	@Override
	public String toString(){
		return "Id: " + getId() + ", email: " + getEmail();
	}

	@Override
	public BigDecimal getId(){
		return new BigDecimal((String) attributes.get("sub"));
	}

	@Override
	public String getEmail(){
		return (String) attributes.get("email");
	}
}