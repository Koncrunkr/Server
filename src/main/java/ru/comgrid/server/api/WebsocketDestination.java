package ru.comgrid.server.api;

import java.math.BigDecimal;

public enum WebsocketDestination{
	USER("/amq/queue/{personId}/user.{id}"),
	TABLE_MESSAGE("/amq/queue/{personId}/table_message.{id}"),
	TABLE_UNION("/amq/queue/{personId}/table_cell_union.{id}"),
	TABLE_DECORATION("/amq/queue/{personId}/table_decoration.{id}");

	private final String destination;

	WebsocketDestination(String destination){
		this.destination = destination;
	}

	public String destination(BigDecimal personId, Object id){
		return destination.replace("{id}", String.valueOf(id))
			.replace("{personId}", personId.toString());
	}
}

