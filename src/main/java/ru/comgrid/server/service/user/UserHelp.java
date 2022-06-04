package ru.comgrid.server.service.user;

import ru.comgrid.server.exception.RequestException;
import ru.comgrid.server.security.user.info.UserPrincipal;

import java.math.BigDecimal;

public enum UserHelp{; // no elements, utility class
    public static BigDecimal extractId(UserPrincipal user){
        return user.getId();
    }
    public static BigDecimal extractId(String userId){
        try{
            return new BigDecimal(userId);
        }catch(NumberFormatException e){
            throw new RequestException(400, "userId.wrong");
        }
    }

    public static boolean samePerson(BigDecimal firstPerson, BigDecimal secondPerson){
        return firstPerson.compareTo(secondPerson) == 0;
    }
}
