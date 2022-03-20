package ru.comgrid.server.exception;

public class ImageWriteToFileException extends RequestException{
    public ImageWriteToFileException() {
        super(500, "image.uploading_error");
    }
}
