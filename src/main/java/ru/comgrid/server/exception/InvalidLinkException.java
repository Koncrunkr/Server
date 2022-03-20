package ru.comgrid.server.exception;

public class InvalidLinkException extends RequestException{
	public InvalidLinkException(){
		super(422, "link.not_allowed");
	}
}
