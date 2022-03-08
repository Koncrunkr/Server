package ru.comgrid.server.exception;

public class ImageEmptyParamException extends WrongRequestException{
    public ImageEmptyParamException() {
        super("image.empty");
    }
}
