package ru.netology.cardwork.dto;

import lombok.Getter;

/**
 * Data to be sent as an offer to confirm the transfer
 */
@Getter
public class OperationIdDto {
    /**
     * A token to signify the operation
     */
    private final String operationId;

    public OperationIdDto(String operationId) {
        this.operationId = operationId;

        System.out.println("OperationId generated: " + this);
    }

    @Override
    public String toString() {
        return '{' + operationId + '}';
    }
}
