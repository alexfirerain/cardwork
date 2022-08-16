package ru.netology.cardwork.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Throws when a transfer operation can't be performed for some reason.
 */
@Slf4j
public class TransferNotPossibleException extends RuntimeException {
    public TransferNotPossibleException(String s) {
        super(s);
        log.debug("TransferNotPossibleException thrown: {}", s);
    }
}
