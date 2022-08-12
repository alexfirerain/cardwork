package ru.netology.cardwork.controller;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.netology.cardwork.dto.ErrorResponseDto;
import ru.netology.cardwork.exception.CardNotFoundException;
import ru.netology.cardwork.exception.FundsInsufficientException;
import ru.netology.cardwork.exception.TransferNotPossibleException;
import ru.netology.cardwork.exception.VerificationFailureException;

import javax.validation.ConstraintViolationException;
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
                        CardNotFoundException.class,
                        MethodArgumentNotValidException.class,
                        HttpMessageNotReadableException.class,
                        ConstraintViolationException.class})
    ResponseEntity<ErrorResponseDto> handleBadRequest(RuntimeException re) {
        log.debug("Caught an exception: {}", re.getClass());
        log.info("Transfer attempt rejected because of inappropriate request: {}", re.getLocalizedMessage());
        return new ResponseEntity<>(new ErrorResponseDto("Error in the request: " + re.getMessage(),
                idCount.getAndIncrement()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransferNotPossibleException.class)
    ResponseEntity<ErrorResponseDto> handleTransferError(TransferNotPossibleException tnpe) {
        log.debug("Caught an exception: {}", tnpe.getClass());
        log.info("Transfer attempt rejected because of error: {}", tnpe.getLocalizedMessage());
        return new ResponseEntity<>(new ErrorResponseDto("Committing the transfer not possible: " + tnpe.getMessage(),
                                                idCount.getAndIncrement()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponseDto> handleServerError(Exception se) {
        log.debug("Caught an exception: {}", se.getClass());
        log.info("Some error at the server: {}", se.getLocalizedMessage());
        return new ResponseEntity<>(new ErrorResponseDto("Error at the server: " + se.getMessage(),
                                                idCount.getAndIncrement()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
