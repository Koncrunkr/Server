package ru.comgrid.server.exception;

public class EditIsNotAllowedException extends WrongRequestException{
	public EditIsNotAllowedException(){
		super("send.edit");
	}
}
