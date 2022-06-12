package ru.netology.cardwork.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cardwork.dto.ConfirmationDto;
import ru.netology.cardwork.dto.OperationIdDto;
import ru.netology.cardwork.dto.Transfer;
import ru.netology.cardwork.service.TransferService;

import javax.validation.Valid;

@RestController
public class TransferController {

    final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public OperationIdDto acceptTransferRequest(@Valid Transfer request) {
        return transferService.bidTransferRequest(request);
    }

    @PostMapping("/confirmOperation")
    public OperationIdDto confirmTransferRequest(@Valid ConfirmationDto confirmation) {
        return transferService.commitTransferRequest(confirmation);
    }


}
