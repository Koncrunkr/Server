package ru.comgrid.server.exception;

public class SendIsNotAllowedException extends WrongRequestException{
	public SendIsNotAllowedException(){
		super("send.publish");
	}
}
