package ru.comgrid.server.exception;

public class EditIsNotAllowedException extends IllegalAccessException{
	public EditIsNotAllowedException(){
		super("send.edit");
	}
}
