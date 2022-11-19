package ru.netology.cardwork.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Identifier of the transaction assigned by the Service
 * and being used for addressing to that transaction while exchanging with messages
 * between the Back and the Front during obtaining confirmation.
 */
@EqualsAndHashCode
@Getter
@Slf4j
public class OperationIdDto {
    /**
     * A token to signify the operation
     */
    public final String operationId;

    public OperationIdDto(String operationId) {
        this.operationId = operationId;
        log.trace("OperationId generated: {}", this);
    }

    @Override
    public String toString() {
        return '{' + operationId + '}';
    }

}
