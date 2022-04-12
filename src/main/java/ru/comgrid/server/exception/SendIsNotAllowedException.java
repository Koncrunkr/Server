package ru.comgrid.server.exception;

public class SendIsNotAllowedException extends IllegalAccessException{
	public SendIsNotAllowedException(){
		super("send.notallowed");
	}
}
