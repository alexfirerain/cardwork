package ru.netology.cardwork.dto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Identifier of the transaction assigned by the Service
 * and being used for addressing to that transaction while exchanging with messages
 * between the Back and the Front during obtaining confirmation.
 */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperationIdDto that = (OperationIdDto) o;
        return operationId.equals(that.operationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationId);
    }
}
