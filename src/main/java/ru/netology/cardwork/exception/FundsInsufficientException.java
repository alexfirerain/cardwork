package ru.netology.cardwork.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FundsInsufficientException extends TransferNotPossibleException {
    public FundsInsufficientException(String s) {
        super(s);
        log.debug("FundsInsufficientException thrown: {}", s);
    }
}
