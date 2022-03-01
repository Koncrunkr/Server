package ru.comgrid.server.api.user;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.math.BigDecimal;

public enum UserHelp{; // no elements, utility class
    @SuppressWarnings("ConstantConditions")
    public static BigDecimal extractId(OAuth2User user){
        return new BigDecimal(((String) user.getAttribute("sub")));
    }

    public static boolean samePerson(BigDecimal firstPerson, BigDecimal secondPerson){
        return firstPerson.compareTo(secondPerson) == 0;
    }
}
