package ru.comgrid.server.exception;

public class ImageDoesNotExistException extends RequestException{
	public ImageDoesNotExistException(String filename){
		super(404, "image.not_found." + filename);
	}
}
