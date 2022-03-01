package ru.comgrid.server.exception;

public class TooBigRequestException extends WrongRequestException{
    public TooBigRequestException(String name, int expectedNumber){
        super("max." + name + "." + expectedNumber);
    }
}
