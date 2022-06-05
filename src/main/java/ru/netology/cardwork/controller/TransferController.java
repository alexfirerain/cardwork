package ru.netology.cardwork.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cardwork.dto.OperationIdDto;
import ru.netology.cardwork.dto.Transfer;

@RestController
public class TransferController {

    @PostMapping("/transfer")
    public ResponseEntity<OperationIdDto> acceptTransferRequest(Transfer request) {
        return null;
    }



}
