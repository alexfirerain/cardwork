package ru.netology.cardwork.service;

import org.springframework.stereotype.Service;
import ru.netology.cardwork.dto.OperationIdDto;
import ru.netology.cardwork.dto.Transfer;
import ru.netology.cardwork.service.provider.OperationIdProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransferService {

    final private Map<OperationIdDto, Transfer> transfersInService;

    final private OperationIdProvider plainOperationIdProvider;

    public TransferService(OperationIdProvider plainOperationIdProvider) {
        this.plainOperationIdProvider = plainOperationIdProvider;
        transfersInService = new ConcurrentHashMap<>();
    }


    public OperationIdDto bidTransferRequest(Transfer request) {
        OperationIdDto operationId = plainOperationIdProvider.serveAnOperationId();
        transfersInService.put(operationId, request);
        return operationId;
    }

}
