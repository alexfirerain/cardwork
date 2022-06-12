package ru.netology.cardwork.service.provider;

import org.springframework.stereotype.Component;

@Component
public class PlainOperationIdProvider implements OperationIdProvider {

    private static long idCount;

    @Override
    public String serveAnOperationId() {
        return String.valueOf(idCount++);
    }

    public long checkCurrentId() {
        return idCount;
    }
    public void resetCount() {
        idCount = 0;
    }
}
