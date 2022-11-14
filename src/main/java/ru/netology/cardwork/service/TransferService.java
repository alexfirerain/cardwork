package ru.netology.cardwork.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.netology.cardwork.dto.ConfirmationDto;
import ru.netology.cardwork.dto.OperationIdDto;
import ru.netology.cardwork.model.Transfer;
import ru.netology.cardwork.exception.VerificationFailureException;
import ru.netology.cardwork.providers.id.OperationIdProvider;
import ru.netology.cardwork.providers.verification.VerificationProvider;
import ru.netology.cardwork.repository.TransferSuitableRepository;

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
     * The source of IDs to assign to operations in service.
     */
    final private OperationIdProvider operationIdProvider;

    /**
     * The performer of verification to send and check verification codes for transfers in service.
     */
    final private VerificationProvider verificationProvider;

    /**
     * A repository this service operates with.
     */
    final private TransferSuitableRepository repository;

    /**
     * A commission rate to be charged on transfers as a double value where 1.0 = 100%.
      */
    @Value("${application.commission}")
    private double COMMISSION_RATE;     // how to make it final? is it needed?

    /**
     * Creates a new transfer service.
     * @param operationIdProvider  a source of ID-sequence to be assigned for served operations.
     * @param verificationProvider  an executor of verifications for transfer operations.
     * @param repository            a repository storing data on accounts with funds.
     */
    public TransferService(OperationIdProvider operationIdProvider,
                           VerificationProvider verificationProvider,
                           TransferSuitableRepository repository) {
        this.operationIdProvider = operationIdProvider;
        this.verificationProvider = verificationProvider;
        this.repository = repository;
        transfersInService = new ConcurrentHashMap<>();
//        COMMISSION_RATE = env.getProperty("${application.commission}");

        log.trace("A Transfer Service initialized");
    }

    /**
     * Accepts a request for transfer, checks in repository if such a transfer is possible,
     * if ok, assigns it to a new operation id (provided via corresponding provider),
     * sets it to the wait list and initiates a verification procedure (via corresponding provider).
     * @param request a request being handled.
     * @return  an OperationIdDto object wrapping newly assigned operation id.
     */
    public OperationIdDto bidTransferRequest(Transfer request) {
        log.debug("Transfer Service received a request: {}", request);
        repository.checkTransferPossibility(request, COMMISSION_RATE);
        verificationProvider
                .performVerificationProcedure(request,
                        repository.getContactData(request.getCardFrom())
                );
        String operationId = operationIdProvider.serveAnOperationId();
        transfersInService.put(operationId, request);
        log.info("transfer request [{}] set to operationId {}", request, operationId);
        return new OperationIdDto(operationId);
    }

    /**
     * Accepts a verification object, sends received code to the verificationProvider,
     * then, if ok, tells the repository to commit the scheduled transfer.
     * @param confirmation a confirmation object received.
     * @return  a new OperationIdDto with the performed operation's ID. If something wrong, throws an exception.
     */
    public OperationIdDto commitTransferRequest(ConfirmationDto confirmation) {     // TODO: wrap in Response Entity
        log.debug("Transfer Service received a confirmation: {}", confirmation);
        String operationId = confirmation.getOperationId();

        if (!transfersInService.containsKey(operationId)) {
            log.error("A code received for an operation that not mapped: id={}", operationId);
            throw new IllegalStateException("Нет операции для подтверждения.");
        }

        Transfer dealToCommit = transfersInService.remove(operationId);

        if (!verificationProvider.isValidCodeForOperation(dealToCommit, confirmation.getCode())) {
            transfersInService.put(operationId, dealToCommit);
            log.warn("A code received does no match to the right one for operation#{}.", operationId);
            throw new VerificationFailureException("Код подтверждения не соответствует.");
        }

        repository.commitTransfer(dealToCommit, COMMISSION_RATE);

        log.info("Operation #{} complete: {}",
                                    operationId,
                                    dealToCommit);
        return new OperationIdDto(operationId);
    }
}
