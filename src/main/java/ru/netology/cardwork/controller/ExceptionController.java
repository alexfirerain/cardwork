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

    /**
     * Handles situations when something is not ok with data in request:
     * constrains on fields are broken or requierd fields missing,
     * when any of cards involved is absent or data of donor card not valid,
     * also when the received verification code is not correct.
     * @param re an exception being caught.
     * @return  a response entity with status code 400 and an ErrorResponseDto as body.
     */
    @ExceptionHandler({VerificationFailureException.class,
                        CardNotFoundException.class,
                        MethodArgumentNotValidException.class,
                        HttpMessageNotReadableException.class,
                        ConstraintViolationException.class})
    ResponseEntity<ErrorResponseDto> handleBadRequest(RuntimeException re) {
        log.debug("Caught an exception: {}", re.getClass());
        log.info("Transfer attempt rejected because of inappropriate request: {}", re.getLocalizedMessage());
        return new ResponseEntity<>(new ErrorResponseDto("Ошибка в запросе: " + re.getMessage(),
                idCount.getAndIncrement()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles situations when the request is ok but the transfer is not possible for some reason:
     * when any of cards involved is inactive or doesn't have a proper currency account, or when funds at donor account
     * are insufficient.
     * @param tnpe an exceptioon being caught.
     * @return  a response entity with status code 500 and an ErrorResponseDto as body.
     */
    @ExceptionHandler(TransferNotPossibleException.class)
    ResponseEntity<ErrorResponseDto> handleTransferError(TransferNotPossibleException tnpe) {
        log.debug("Caught an exception: {}", tnpe.getClass());
        log.info("Transfer attempt rejected because of error: {}", tnpe.getLocalizedMessage());
        return new ResponseEntity<>(new ErrorResponseDto("Совершение перевода невозможно: " + tnpe.getMessage(),
                                                idCount.getAndIncrement()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles all other exceptional cases that are likely during server's exploitation.
     * Particulary a situation when a verification code comes for the operation whose id is not at the wait list somehow.
     * @param se an exception being caught.
     * @return a response entity with status code 500 and an ErrorResponseDto as body.
     */
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponseDto> handleServerError(Exception se) {
        log.debug("Caught an exception: {}", se.getClass());
        log.info("Some error at the server: {}", se.getLocalizedMessage());
        return new ResponseEntity<>(new ErrorResponseDto("Error at the server: " + se.getMessage(),
                                                idCount.getAndIncrement()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
