package ru.netology.cardwork.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.netology.cardwork.dto.ErrorResponseDto;

@ControllerAdvice
public class ExceptionController {
    private static int idCount; // volatile?



    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorResponseDto> handleBadRequest(IllegalArgumentException iae) {
        return new ResponseEntity<>(
                new ErrorResponseDto("Error in the request" + iae.getMessage(), idCount++),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponseDto> handleServerError(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponseDto("Error at the server" + e.getMessage(), idCount++),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
