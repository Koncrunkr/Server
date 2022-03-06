package ru.comgrid.server.exception;

public class IllegalAccessException extends WrongRequestException{
    public IllegalAccessException(String subject){
        super("access." + subject);
    }
}
