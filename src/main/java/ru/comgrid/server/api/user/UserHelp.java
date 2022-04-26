package ru.comgrid.server.api.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import ru.comgrid.server.security.user.info.UserPrincipal;

import java.math.BigDecimal;

public enum UserHelp{; // no elements, utility class
    public static BigDecimal extractId(UserPrincipal user){
        return user.getId();
    }

    public static boolean samePerson(BigDecimal firstPerson, BigDecimal secondPerson){
        return firstPerson.compareTo(secondPerson) == 0;
    }
}
