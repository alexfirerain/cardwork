package ru.netology.cardwork.service;

import org.springframework.stereotype.Service;
import ru.netology.cardwork.dto.ConfirmationDto;
import ru.netology.cardwork.dto.OperationIdDto;
import ru.netology.cardwork.dto.Transfer;
import ru.netology.cardwork.repository.AccountsRepository;
import ru.netology.cardwork.service.provider.OperationIdProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransferService {

    final private Map<String, Transfer> transfersInService;

    final private OperationIdProvider plainOperationIdProvider;
    final private AccountsRepository accountsRepository;

    public TransferService(OperationIdProvider plainOperationIdProvider, AccountsRepository accountsRepository) {
        this.plainOperationIdProvider = plainOperationIdProvider;
        this.accountsRepository = accountsRepository;
        transfersInService = new ConcurrentHashMap<>();
    }


    public OperationIdDto bidTransferRequest(Transfer request) {
        String operationId = plainOperationIdProvider.serveAnOperationId();
        transfersInService.put(operationId, request);
        return new OperationIdDto(operationId);
    }

    public OperationIdDto commitTransferRequest(ConfirmationDto confirmation) {
        String operationId = confirmation.getOperationId();
        Transfer dealToCommit = transfersInService.get(operationId);
        accountsRepository.commitTransfer(dealToCommit);
        return new OperationIdDto(operationId);
    }
}
