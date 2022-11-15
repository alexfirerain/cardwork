package ru.netology.cardwork.providers.id;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A simple implementation of OperationId generator producing simple consequent numbers.
 */
@Component
@Slf4j
public class OperationIdProviderDemoImpl implements OperationIdProvider {

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

        Long next = idCount.getAndIncrement();
        log.trace("served an ID: {}", next);

        return String.valueOf(next);
    }

    /**
     * Reports the ID that is to be assigned at next invocation.
     * @return  the next ID to be assigned for a transaction.
     */
    public long checkCurrentId() {
        return idCount.get();
    }

    /**
     * Resets the ID-counter to 0.
     */
    public void resetCount() {
        idCount.set(0);
    }
}
