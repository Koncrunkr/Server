package ru.comgrid.server.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class CustomOidcUser extends DefaultOidcUser implements UserDetails{
	private final String name;
	public CustomOidcUser(OidcUser user){
		super(user.getAuthorities(), user.getIdToken(), user.getUserInfo());
		this.name = user.getName();
	}

	@Override
	public String getName(){
		return name;
	}

	@Override
	public String toString(){
		return name;
	}

	@Override
	public String getPassword(){
		return null;
	}

	@Override
	public String getUsername(){
		return getName();
	}

	@Override
	public boolean isAccountNonExpired(){
		return true;
	}

	@Override
	public boolean isAccountNonLocked(){
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired(){
		return true;
	}

	@Override
	public boolean isEnabled(){
		return true;
	}
}
