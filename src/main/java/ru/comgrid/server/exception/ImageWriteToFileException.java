package ru.comgrid.server.exception;

public class ImageWriteToFileException extends WrongRequestException{
    public ImageWriteToFileException() {
        super("image.uploading");
    }
}
