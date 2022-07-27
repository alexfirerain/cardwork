package ru.netology.cardwork.controller;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.netology.cardwork.dto.ErrorResponseDto;
import ru.netology.cardwork.exception.TransferNotPossibleException;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handler of exceptional cases
 */
@ControllerAdvice
@NoArgsConstructor
public class ExceptionController {
    private static final AtomicInteger idCount = new AtomicInteger();

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    ErrorResponseDto handleBadRequest(IllegalArgumentException iae) {
        return new ErrorResponseDto("Error in the request: " + iae.getMessage(),
                                                idCount.getAndIncrement());
    }

    @ExceptionHandler(TransferNotPossibleException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponseDto handleTransferError(TransferNotPossibleException tnpe) {
        return new ErrorResponseDto("Committing the transfer not possible: " + tnpe.getMessage(),
                                                idCount.getAndIncrement());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponseDto handleServerError(Exception se) {
        return new ErrorResponseDto("Error at the server: " + se.getMessage(),
                                                idCount.getAndIncrement());
    }

}
