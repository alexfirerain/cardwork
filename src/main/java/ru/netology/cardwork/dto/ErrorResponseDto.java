package ru.netology.cardwork.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data to be sent as an info on errors
 */
@AllArgsConstructor
@Getter
public class ErrorResponseDto {
    /**
     * Error customer message
     */
    private final String message;
    /**
     * Error id
     */
    private final Integer id;

}
