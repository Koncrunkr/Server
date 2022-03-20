package ru.comgrid.server.exception;

public class CellUnionAlreadyExistsException extends RequestException{
	public CellUnionAlreadyExistsException(){
		super(422, "cell_union.exists");
	}
}
