package ru.comgrid.server.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ControllerAdvice
@ResponseBody
public class ExceptionHandlingController{


    @ApiResponse(content = @Content(), responseCode = "500")
    @ResponseStatus(value= HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler({WrongRequestException.class})
    public String commonException(Exception exception){
        return "{\"timestamp\": \"" + LocalDateTime.now() + "\", \"status\": 422, \"reason\": \"" + exception.getMessage() + "\"}";
    }
}
