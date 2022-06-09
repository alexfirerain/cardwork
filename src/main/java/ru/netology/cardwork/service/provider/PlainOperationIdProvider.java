package ru.netology.cardwork.service.provider;

import org.springframework.stereotype.Component;
import ru.netology.cardwork.dto.OperationIdDto;

@Component
public class PlainOperationIdProvider implements OperationIdProvider {

    private static long idCount;

    @Override
    public OperationIdDto serveAnOperationId() {
        return new OperationIdDto(String.valueOf(idCount++));
    }
}
