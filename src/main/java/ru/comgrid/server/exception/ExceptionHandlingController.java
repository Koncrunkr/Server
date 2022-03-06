package ru.comgrid.server.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ControllerAdvice
@ResponseBody
public class ExceptionHandlingController{


    @ApiResponse(content = @Content(), responseCode = "500")
    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    @ExceptionHandler({WrongRequestException.class})
    public String commonException(Exception exception){
        return "{\"timestamp\": \"" + LocalDateTime.now() + "\", \"status\": 500, \"reason\": \"" + exception.getMessage() + "\"}";
    }
}
