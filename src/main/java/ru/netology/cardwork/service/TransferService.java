package ru.netology.cardwork.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.cardwork.dto.ConfirmationDto;
import ru.netology.cardwork.dto.OperationIdDto;
import ru.netology.cardwork.dto.Transfer;
import ru.netology.cardwork.exception.VerificationFailureException;
import ru.netology.cardwork.providers.verification.VerificationProvider;
import ru.netology.cardwork.repository.AccountsRepository;
import ru.netology.cardwork.providers.id.OperationIdProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A service handling transfer requests. It accepts a request first and, on confirmation, sends it for release next.
 */
@Service
@Slf4j
public class TransferService {

    /**
     * The list of transaction requests waiting to be confirmed.
     */
    final private Map<String, Transfer> transfersInService;

    /**
     * The source of operational IDs to assign to the transfers in service.
     */
    final private OperationIdProvider operationIdProvider;

    /**
     * The source of verification codes to verify the transfers in service.
     */
    final private VerificationProvider verificationProvider;

    /**
     * A repository this service operates with.
     */
    final private AccountsRepository accountsRepository;

    public TransferService(OperationIdProvider operationIdProvider,
                           VerificationProvider verificationProvider,
                           AccountsRepository accountsRepository) {
        this.operationIdProvider = operationIdProvider;
        this.verificationProvider = verificationProvider;
        this.accountsRepository = accountsRepository;
        transfersInService = new ConcurrentHashMap<>();

        log.debug("A Transfer Service initialized");
    }

    /**
     * Accepts a request for transfer, checks in repository if such a transfer is possible,
     * if ok, assigns it to a new operation id (provided via corresponding provider),
     * sets it to the wait list and initiates a verification procedure (via corresponding provider).
     * @param request a request being handled.
     * @return  an OperationIdDto object wrapping newly assigned operation id.
     */
    public OperationIdDto bidTransferRequest(Transfer request) {
        log.debug("TS received a request: {}", request);
        accountsRepository.checkTransferPossibility(request);
        String operationId = operationIdProvider.serveAnOperationId();
        transfersInService.put(operationId, request);
        verificationProvider
                .performVerificationProcedure(request,
                        accountsRepository.getContactData(request.getCardFrom())
                );
        log.info("transfer request {} set to operationId {}", request, operationId);
        return new OperationIdDto(operationId);
    }

    public OperationIdDto commitTransferRequest(ConfirmationDto confirmation) {
        log.debug("TS received a confirmation: {}", confirmation);
        String operationId = confirmation.getOperationId();

        if (!transfersInService.containsKey(operationId)) {
            log.error("A code received for an operation that not mapped.");
            throw new IllegalStateException("Нет операции для подтверждения.");
        }

        Transfer dealToCommit = transfersInService.remove(operationId);

        if (!verificationProvider.validate(dealToCommit, confirmation.getCode())) {
            transfersInService.put(operationId, dealToCommit);
            log.warn("A code received does no match to the right one for operation#{}.", operationId);
            throw new VerificationFailureException("Код подтверждения не соответствует.");
        }

        accountsRepository.commitTransfer(dealToCommit);

        log.info("Transfer committed: {}", dealToCommit);
        return new OperationIdDto(operationId);
    }
}
