package ru.comgrid.server.exception;

public class NotFoundException extends RequestException{
	public NotFoundException(String message){
		super(404, message);
	}
}
