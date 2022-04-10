package ru.comgrid.server.api.message;

public enum WebsocketDestination{
	USER("/connection/user/"),
	TABLE_MESSAGE("/connection/table_message/"),
	TABLE_UNION("/connection/table_cell_union/");

	private final String destination;

	WebsocketDestination(String destination){
		this.destination = destination;
	}

	public String destination(Object id){
		return destination + id;
	}
}

