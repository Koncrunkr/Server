package ru.comgrid.server.exception;

public class MessageNotFoundException extends RequestException{
	public MessageNotFoundException(){
		super(404, "message.not_found");
	}
}
