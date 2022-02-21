package ru.comgrid.server.exception;

public class OutOfBoundsRequestException extends WrongRequestException{
    public OutOfBoundsRequestException(String message){
        super(message);
    }
    public OutOfBoundsRequestException(){
        super("Some values are out of bounds");
    }
}
