package ru.netology.cardwork.controller;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.netology.cardwork.dto.ErrorResponseDto;
import ru.netology.cardwork.exception.CardNotFoundException;
import ru.netology.cardwork.exception.FundsInsufficientException;
import ru.netology.cardwork.exception.TransferNotPossibleException;
import ru.netology.cardwork.exception.VerificationFailureException;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handler of exceptional cases
 */
@ControllerAdvice
@NoArgsConstructor
@Slf4j
public class ExceptionController {
    private static final AtomicInteger idCount = new AtomicInteger();

    @ExceptionHandler({VerificationFailureException.class,
                        FundsInsufficientException.class,
                        CardNotFoundException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    ErrorResponseDto handleBadRequest(RuntimeException re) {
        log.info("Transfer attempt rejected because of inappropriate request: {}", re.getLocalizedMessage());
        return new ErrorResponseDto("Error in the request: " + re.getMessage(),
                                                idCount.getAndIncrement());
    }

    @ExceptionHandler(TransferNotPossibleException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponseDto handleTransferError(TransferNotPossibleException tnpe) {
        log.info("Transfer attempt rejected because of error: {}", tnpe.getLocalizedMessage());
        return new ErrorResponseDto("Committing the transfer not possible: " + tnpe.getMessage(),
                                                idCount.getAndIncrement());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponseDto handleServerError(Exception se) {
        log.info("Some error at the server: {}", se.getLocalizedMessage());
        return new ErrorResponseDto("Error at the server: " + se.getMessage(),
                                                idCount.getAndIncrement());
    }

}
