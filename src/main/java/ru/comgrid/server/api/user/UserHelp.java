package ru.comgrid.server.api.user;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.math.BigInteger;

public enum UserHelp{; // no elements, utility class
    public static BigInteger extractId(OAuth2User user){
        return new BigInteger(((String) user.getAttributes().get("sub")));
    }
}
