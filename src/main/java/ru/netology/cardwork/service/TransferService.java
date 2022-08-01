package ru.netology.cardwork.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.cardwork.dto.ConfirmationDto;
import ru.netology.cardwork.dto.OperationIdDto;
import ru.netology.cardwork.dto.Transfer;
import ru.netology.cardwork.exception.CodeNotFitsException;
import ru.netology.cardwork.providers.VerificationCodeProvider;
import ru.netology.cardwork.repository.AccountsRepository;
import ru.netology.cardwork.providers.OperationIdProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TransferService {

    final private Map<String, Transfer> transfersInService;

    final private OperationIdProvider operationIdProvider;
    final private VerificationCodeProvider verificationCodeProvider;
    final private AccountsRepository accountsRepository;

    public TransferService(OperationIdProvider operationIdProvider, VerificationCodeProvider verificationCodeProvider, AccountsRepository accountsRepository) {
        this.operationIdProvider = operationIdProvider;
        this.verificationCodeProvider = verificationCodeProvider;
        this.accountsRepository = accountsRepository;
        transfersInService = new ConcurrentHashMap<>();

        log.debug("A Transfer Service initialized");
    }


    public OperationIdDto bidTransferRequest(Transfer request) {

        log.debug("TS received a request: {}", request);

        String operationId = operationIdProvider.serveAnOperationId();
        transfersInService.put(operationId, request);

        return new OperationIdDto(operationId);
    }

    public OperationIdDto commitTransferRequest(ConfirmationDto confirmation) {
        String operationId = confirmation.getOperationId();
        String codeReceived = confirmation.getVerificationCode();
        if (!verificationCodeProvider.accepts(codeReceived)) {
            throw new CodeNotFitsException("The received code doesn't match");
        }
        Transfer dealToCommit = transfersInService.get(operationId);
        accountsRepository.commitTransfer(dealToCommit);
        return new OperationIdDto(operationId);
    }
}
