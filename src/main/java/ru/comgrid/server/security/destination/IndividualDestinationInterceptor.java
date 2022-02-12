package ru.comgrid.server.security.destination;

import java.math.BigDecimal;

public interface IndividualDestinationInterceptor{
    String destination();
    boolean hasAccess(BigDecimal userId, String destinationId);
}
