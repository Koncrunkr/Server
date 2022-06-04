package ru.comgrid.server.security.websocket.destination;

import java.math.BigDecimal;

public interface IndividualDestinationInterceptor{
    String destination();
    boolean hasAccess(BigDecimal userId, String destinationId);
}
