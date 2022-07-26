package ru.netology.cardwork.service;

import org.springframework.stereotype.Service;
import ru.netology.cardwork.dto.ConfirmationDto;
import ru.netology.cardwork.dto.OperationIdDto;
import ru.netology.cardwork.dto.Transfer;
import ru.netology.cardwork.repository.AccountsRepository;
import ru.netology.cardwork.providers.OperationIdProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransferService {

    final private Map<String, Transfer> transfersInService;

    final private OperationIdProvider operationIdProvider;
    final private AccountsRepository accountsRepository;

    public TransferService(OperationIdProvider operationIdProvider, AccountsRepository accountsRepository) {
        this.operationIdProvider = operationIdProvider;
        this.accountsRepository = accountsRepository;
        transfersInService = new ConcurrentHashMap<>();

        System.out.println("TS initialized");
    }


    public OperationIdDto bidTransferRequest(Transfer request) {

        System.out.println("TS received a request: " + request);

        String operationId = operationIdProvider.serveAnOperationId();
        transfersInService.put(operationId, request);

        System.out.println("operation id = " + operationId);

        return new OperationIdDto(operationId);
    }

    public OperationIdDto commitTransferRequest(ConfirmationDto confirmation) {
        String operationId = confirmation.getOperationId();
        Transfer dealToCommit = transfersInService.get(operationId);
        accountsRepository.commitTransfer(dealToCommit);
        return new OperationIdDto(operationId);
    }
}
