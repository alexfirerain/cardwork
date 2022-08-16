package ru.netology.cardwork.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Throws when the amount of funds on an account is not sufficient for certain operation to complete.
 */
@Slf4j
public class FundsInsufficientException extends TransferNotPossibleException {
    public FundsInsufficientException(String s) {
        super(s);
        log.debug("FundsInsufficientException thrown: {}", s);
    }
}
