package ru.comgrid.server.exception;

public class OutOfBoundsRequestException extends RequestException{
    public OutOfBoundsRequestException(){
        super(422, "out_of_bounds");
    }
    public OutOfBoundsRequestException(String message){
        super(422, message);
    }
}
