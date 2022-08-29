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
//@RequestMapping("/transfer")
public class TransferController {

    /**
     * A service this controller operates upon.
     */
    final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
        log.trace("A Transfer Controller initialized");
    }

    @PostMapping("/transfer")
    @CrossOrigin(origins = "http://localhost:3000")
    public OperationIdDto acceptTransferRequest(@RequestBody @Valid Transfer request) {
        log.info("a request received: {}", request);
        return transferService.bidTransferRequest(request);
    }

    @PostMapping("/confirmOperation")
    @CrossOrigin(origins = "http://localhost:3000")
    public OperationIdDto confirmTransferRequest(@RequestBody @Valid ConfirmationDto confirmation) {
        log.info("a confirmation received: {}", confirmation);
        return transferService.commitTransferRequest(confirmation);
    }


}
