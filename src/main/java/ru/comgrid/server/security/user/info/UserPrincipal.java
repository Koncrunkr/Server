package ru.comgrid.server.security.user.info;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import ru.comgrid.server.model.Person;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ru.comgrid.server.security.UserRole.ROLE_USER;

public class UserPrincipal implements OAuth2User, UserDetails{
	private static final SimpleGrantedAuthority roleUser = new SimpleGrantedAuthority(ROLE_USER);
	private final BigDecimal id;
	private final String name;
	private final String email;
	private final Collection<? extends GrantedAuthority> authorities;
	private final Map<String, Object> attributes;

	public UserPrincipal(BigDecimal id, String name, String email, Collection<? extends GrantedAuthority> authorities){
		this.id = id;
		this.email = email;
		this.name = name;
		this.authorities = authorities;
		attributes = Map.of();
	}

	public UserPrincipal(BigDecimal id, String name, String email, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes){
		this.id = id;
		this.email = email;
		this.name = name;
		this.authorities = authorities;
		this.attributes = attributes;
	}

	public static UserPrincipal create(Person person){
		return new UserPrincipal(
			person.getId(),
			person.getName(),
			person.getEmail(),
			requireRoleUser(person.getAuthorities())
		);
	}

	public static UserPrincipal create(Person person, Map<String, Object> attributes){
		return new UserPrincipal(
			person.getId(),
			person.getName(),
			person.getEmail(),
			requireRoleUser(person.getAuthorities()),
			attributes
		);
	}

	private static List<GrantedAuthority> requireRoleUser(List<GrantedAuthority> authorities){
		if(authorities == null)
			authorities = new ArrayList<>(1);
		if(!authorities.contains(roleUser))
			authorities.add(roleUser);
		return authorities;
	}

	@Override
	public String getUsername(){
		return getId().toString();
	}

	public BigDecimal getId(){
		return id;
	}

	@Override
	public String getPassword(){
		return null;
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

	@Override
	public Map<String, Object> getAttributes(){
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities(){
		return authorities;
	}

	@Override
	public String getName(){
		return name;
	}

	public boolean containsAuthority(GrantedAuthority authority){
		return getAuthorities().contains(authority);
	}
}
