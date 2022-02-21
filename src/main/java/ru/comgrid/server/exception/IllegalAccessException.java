package ru.comgrid.server.exception;

public class IllegalAccessException extends RuntimeException{
    public IllegalAccessException(String subject){
        super("Sorry, you don't have access " + subject);
    }
}
