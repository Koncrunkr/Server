package ru.comgrid.server.exception;

public class CellUnionAlreadyExistsException extends WrongRequestException{
	public CellUnionAlreadyExistsException(){
		super("cell_union.exists");
	}
}
