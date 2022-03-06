package ru.comgrid.server.exception;

public class MessageAlreadyExistsException extends WrongRequestException{
	public MessageAlreadyExistsException(){
		super("message.exists");
	}
}
