package ru.netology.cardwork.dto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Data to be sent as an info on errors
 */
@Getter
@Slf4j
public class ErrorResponseDto {
    /**
     * Error customer message
     */
    private final String message;
    /**
     * Error id
     */
    private final Integer id;

    public ErrorResponseDto(String message, Integer id) {
        this.message = message;
        this.id = id;
        log.trace("error response generated: {}", this);
    }

    @Override
    public String toString() {
        return "Error#" + id + ": " + message;
    }
}
