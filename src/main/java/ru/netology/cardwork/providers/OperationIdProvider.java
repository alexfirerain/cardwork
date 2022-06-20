package ru.netology.cardwork.providers;

/**
 * A source of sequence of operation-IDs to be assigned to new transfer deals
 */
public interface OperationIdProvider {
    String serveAnOperationId();
}
