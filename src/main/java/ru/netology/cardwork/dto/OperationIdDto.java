package ru.netology.cardwork.dto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Data to be sent as an offer to confirm the transfer
 */
@Getter
@Slf4j
public class OperationIdDto {
    /**
     * A token to signify the operation
     */
    private final String operationId;

    public OperationIdDto(String operationId) {
        this.operationId = operationId;
        log.debug("OperationId generated: {}", this);
    }

    @Override
    public String toString() {
        return '{' + operationId + '}';
    }
}
