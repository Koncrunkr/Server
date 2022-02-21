package ru.comgrid.server.exception;

public class TooBigRequestException extends WrongRequestException{
    public TooBigRequestException(String name, int expectedNumber){
        super("Number of " + name + " has to be not greater than " + expectedNumber);
    }
}
