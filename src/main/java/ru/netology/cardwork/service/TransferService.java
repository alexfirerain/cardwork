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

    /**
     * The list of transfer requests waiting to be confirmed.
     */
    final private Map<String, Transfer> transfersInService;

    /**
     * The source of operational IDs to assign to the transfers in service.
     */
    final private OperationIdProvider operationIdProvider;

    /**
     * The source of verification codes to verify the transfers in service.
     */
    final private VerificationCodeProvider verificationCodeProvider;

    /**
     * A repository this service operates with.
     */
    final private AccountsRepository accountsRepository;

    public TransferService(OperationIdProvider operationIdProvider,
                           VerificationCodeProvider verificationCodeProvider,
                           AccountsRepository accountsRepository) {
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

        log.debug("TS received a confirmation: {}", confirmation);
        String operationId = confirmation.getOperationId();
        String codeReceived = confirmation.getVerificationCode();

        if (!transfersInService.containsKey(operationId)) {
            throw new RuntimeException("Operation not in service");
        }
        if (!verificationCodeProvider.accepts(codeReceived)) {
            throw new CodeNotFitsException("The received code doesn't match");
        }

        Transfer dealToCommit = transfersInService.remove(operationId);
        accountsRepository.commitTransfer(dealToCommit);

        log.info("Transfer committed: {}", dealToCommit);
        return new OperationIdDto(operationId);
    }
}
