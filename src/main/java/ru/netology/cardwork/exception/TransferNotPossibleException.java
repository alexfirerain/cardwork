package ru.netology.cardwork.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferNotPossibleException extends RuntimeException {
    public TransferNotPossibleException(String s) {
        super(s);
        log.debug("TransferNotPossibleException thrown: {}", s);
    }
}
