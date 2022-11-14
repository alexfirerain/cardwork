package ru.netology.cardwork.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.netology.cardwork.dto.ConfirmationDto;
import ru.netology.cardwork.dto.OperationIdDto;
import ru.netology.cardwork.dto.Transfer;
import ru.netology.cardwork.service.TransferService;

import javax.validation.Valid;

/**
 * A REST-controller to receive and handle requests on money transfer.
 */
@Slf4j
@RestController
@CrossOrigin(origins = "${application.front-url}")
public class TransferController {

    /**
     * A service this controller operates upon.
     */
    final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
        log.trace("A Transfer Controller initialized");
//        log.info("Starting listening at localhost:{} for {}", "${server.port}", "${application.front-url}");
    }

    /**
     *  Validates and accepts an incoming transfer request
     *  forwarding it to the Service
     *  and returning a string of OperationId wrapped in a OperationIdDto object.
     * @param request a Transfer to accept.
     * @return  an ID for accepted transfer request, defined and assigned by the Service.
     */
    @PostMapping("/transfer")
    public OperationIdDto acceptTransferRequest(@RequestBody @Valid Transfer request) {
        log.info("a request received: {}", request);
        return transferService.bidTransferRequest(request);
    }

    /**
     * Validates and accepts an incoming confirmation request
     * forwarding it to the Service
     * and returning a wrapped in a OperationIdDto object string of OperationId
     * of the corresponding performed transaction.
     * @param confirmation a confirmation object to apply to the pending operation.
     * @return the ID for confirmed transfer request.
     */
    @PostMapping("/confirmOperation")
    public OperationIdDto confirmTransferRequest(@RequestBody @Valid ConfirmationDto confirmation) {
        log.info("a confirmation received: {}", confirmation);
        return transferService.commitTransferRequest(confirmation);
    }


}
