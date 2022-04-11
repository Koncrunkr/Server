package ru.comgrid.server.exception;

public class InvalidLinkException extends RequestException{
	public InvalidLinkException(){
		super(422, "link.invalid");
	}
	public InvalidLinkException(String fileName){
		super(422, "invalid.not_allowed." + fileName);
	}
	public InvalidLinkException(String fileName, Throwable e){
		super(422, "link.invalid." + fileName + ", " + e.getClass() + "@" + e.getMessage());
	}
}
