package ru.comgrid.server.security.destination;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class UserDestinationInterceptor implements IndividualDestinationInterceptor{
	@Override
	public String destination(){
		return "user";
	}

	@Override
	public boolean hasAccess(BigDecimal userId, String destinationId){
		return destinationId.equals(userId.toString());
	}
}
