package ru.netology.cardwork.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.netology.cardwork.dto.ErrorResponseDto;

import java.util.concurrent.atomic.AtomicInteger;

@ControllerAdvice
public class ExceptionController {
    private static AtomicInteger idCount;

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    ErrorResponseDto handleBadRequest(IllegalArgumentException iae) {
        return new ErrorResponseDto("Error in the request: " + iae.getMessage(),
                                                idCount.getAndIncrement());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponseDto handleServerError(Exception e) {
        return new ErrorResponseDto("Error at the server: " + e.getMessage(),
                                                idCount.getAndIncrement());
    }

}
