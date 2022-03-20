package ru.comgrid.server.exception;

public class ImageEmptyParamException extends RequestException{
    public ImageEmptyParamException() {
        super(400, "image.empty");
    }
}
