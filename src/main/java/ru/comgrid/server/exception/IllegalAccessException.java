package ru.comgrid.server.exception;

public class IllegalAccessException extends RequestException{
    public IllegalAccessException(String subject){
        super(403, "access." + subject);
    }
}
