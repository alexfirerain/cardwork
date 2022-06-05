package ru.netology.cardwork.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data to be sent as an offer to confirm the transfer
 */
@AllArgsConstructor
@Getter
public class OperationIdDto {
    /**
     * A token to signify the operation
     */
    private final String operationId;
}
