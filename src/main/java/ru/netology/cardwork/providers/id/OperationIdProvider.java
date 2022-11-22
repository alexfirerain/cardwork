package ru.netology.cardwork.providers.id;

/**
 * A source of sequence of operation-IDs to be assigned to new transfer deals
 */
public interface OperationIdProvider {
    /**
     * Supplies an operational ID for a new operation.
     * @return a string that is an operational ID.
     */
    String serveAnOperationId();
}
