package ru.comgrid.server.security;

import org.springframework.data.util.Pair;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;

class CustomUserDetails extends DefaultOidcUser implements UserDetails{
    private final String name;

    public CustomUserDetails(OidcUser user){
        this(user.getAuthorities(), user.getIdToken(), user.getUserInfo(), user.getName());
    }
    public CustomUserDetails(
        Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken,
        OidcUserInfo userInfo, String name
    ){
        super(authorities, idToken, userInfo);
        this.name = name;
    }

    @Override
    public String getPassword(){
        return this.getIdToken().getTokenValue();
    }

    @Override
    public String getUsername(){
        return toString();
    }

    @Override
    public String getName(){
        return name;
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
