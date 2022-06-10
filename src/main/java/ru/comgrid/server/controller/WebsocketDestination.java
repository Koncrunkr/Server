package ru.comgrid.server.controller;

import java.math.BigDecimal;

public enum WebsocketDestination{
	USER("/queue/user.{id}"),
	TABLE_MESSAGE("/queue/table_message.{id}"),
	TABLE_UNION("/queue/table_cell_union.{id}"),
	TABLE_DECORATION("/queue/table_decoration.{id}");

	private final String destination;

	WebsocketDestination(String destination){
		this.destination = destination;
	}

	public String destination(BigDecimal personId, Object id){
		return destination.replace("{id}", String.valueOf(id))
			.replace("{personId}", personId.toString());
	}
}

