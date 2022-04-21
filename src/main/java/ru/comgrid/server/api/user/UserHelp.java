package ru.comgrid.server.api.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.math.BigDecimal;

public enum UserHelp{; // no elements, utility class
    public static BigDecimal extractId(UserDetails user){
        return new BigDecimal(user.getUsername());
    }

    public static boolean samePerson(BigDecimal firstPerson, BigDecimal secondPerson){
        return firstPerson.compareTo(secondPerson) == 0;
    }
}
