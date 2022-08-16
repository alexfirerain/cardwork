package ru.netology.cardwork.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Throws when a card number is requested which not present in a repository.
 */
@Slf4j
public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(String s) {
        super(s);
        log.debug("CardNotFoundException thrown: {}", s);
    }
}
