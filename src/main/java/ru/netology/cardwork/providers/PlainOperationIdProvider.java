package ru.netology.cardwork.providers;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
@NoArgsConstructor
public class PlainOperationIdProvider implements OperationIdProvider {

    /**
     * A counter to serve a next number as an ID.
     */
    private static final AtomicLong idCount = new AtomicLong();

    /**
     * Provides a following integer as an ID.
     * @return a string presentation of number of operations having been initialized before this call.
     */
    @Override
    public String serveAnOperationId() {
        return String.valueOf(idCount.getAndIncrement());
    }

    public long checkCurrentId() {
        return idCount.get();
    }
    public void resetCount() {
        idCount.set(0);
    }
}
