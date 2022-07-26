package ru.netology.cardwork.providers;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class PlainOperationIdProvider implements OperationIdProvider {

    /**
     * A counter to serve a next number as an ID.
     */
    private static final AtomicLong idCount = new AtomicLong();

    public PlainOperationIdProvider() {
        System.out.println("OIP initialized");
    }

    /**
     * Provides a following integer as an ID.
     * @return a string presentation of number of operations having been initialized before this call.
     */
    @Override
    public String serveAnOperationId() {

        Long next = idCount.getAndIncrement();
        System.out.println("served an ID: " + next);

        return String.valueOf(next);
    }

    public long checkCurrentId() {
        return idCount.get();
    }
    public void resetCount() {
        idCount.set(0);
    }
}
