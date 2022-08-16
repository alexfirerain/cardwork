package ru.netology.cardwork.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Throws when a verification procedure fails.
 */
@Slf4j
public class VerificationFailureException extends RuntimeException {
    public VerificationFailureException(String s) {
        super(s);
        log.debug("VerificationFailureException is thrown: {}", s);
    }
}
