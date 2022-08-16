package ru.netology.cardwork.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Throws when the card data provided don't match to the one in a repository.
 */
@Slf4j
public class CardDataNotValidException extends RuntimeException {
    public CardDataNotValidException(String s) {
        super(s);
        log.debug("CardDataNotValidException thrown: {}", s);
    }
}
