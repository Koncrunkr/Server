package ru.comgrid.server.exception;

public class MessageAlreadyExistsException extends RequestException{
	public MessageAlreadyExistsException(){
		super(422, "message.exists");
	}
}
