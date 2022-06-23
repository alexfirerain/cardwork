package ru.netology.cardwork.providers;

import org.springframework.stereotype.Component;

/**
 * A source of sequence of operation-IDs to be assigned to new transfer deals
 */
@Component
public interface OperationIdProvider {
    String serveAnOperationId();
}
