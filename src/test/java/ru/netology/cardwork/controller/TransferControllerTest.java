package ru.netology.cardwork.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.cardwork.dto.ConfirmationDto;
import ru.netology.cardwork.dto.OperationIdDto;
import ru.netology.cardwork.providers.verification.VerificationProviderDemoImpl;
import ru.netology.cardwork.service.TransferService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.netology.cardwork.repository.DemoData.*;

class TransferControllerTest {

    private TransferController transferController;
    private final TransferService transferService = Mockito.mock(TransferService.class);

    @BeforeEach
    void setUp() {
        transferController = new TransferController(transferService);
    }

    @Test
    void at_1stRequest_returnsId0Dto() {
        OperationIdDto expected = new OperationIdDto("0");
        when(transferService.bidTransferRequest(any())).thenReturn(expected);

        assertEquals(expected, transferController.acceptTransferRequest(TRANSFER_1));
    }

    @Test
    void at_successfulConfirmation_returnsIdSameDto() {
        OperationIdDto expected = new OperationIdDto("7");
        ConfirmationDto confirmation = new ConfirmationDto("7",
                                                VerificationProviderDemoImpl.provideACode());
        when(transferService.commitTransferRequest(confirmation)).thenReturn(expected);

        assertEquals(expected, transferController.confirmTransferRequest(confirmation));
    }
}