package ru.comgrid.server.exception;

public class MessageNotFoundException extends WrongRequestException{
	public MessageNotFoundException(){
		super("message.not_found");
	}
}
