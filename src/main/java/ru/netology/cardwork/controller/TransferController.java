package ru.netology.cardwork.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
@RequestMapping("/transfer")
public class TransferController {

    /**
     * A service this controller operates upon.
     */
    final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
        log.debug("A Transfer Controller initialized");
    }

    @PostMapping("/transfer")
    public OperationIdDto acceptTransferRequest(@RequestBody @Valid Transfer request) {

        log.info("a request {} received", request);

        return transferService.bidTransferRequest(request);
    }

    @PostMapping("/confirmOperation")
    public OperationIdDto confirmTransferRequest(@RequestBody @Valid ConfirmationDto confirmation) {

        log.info("a confirmation {} received ", confirmation);

        return transferService.commitTransferRequest(confirmation);
    }


}
