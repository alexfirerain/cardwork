package ru.netology.cardwork.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.netology.cardwork.dto.ConfirmationDto;
import ru.netology.cardwork.dto.OperationIdDto;
import ru.netology.cardwork.providers.id.OperationIdProvider;
import ru.netology.cardwork.providers.verification.VerificationProvider;
import ru.netology.cardwork.repository.TransferSuitableRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.netology.cardwork.repository.DemoData.TRANSFER_1;

@SpringBootTest
class TransferServiceTest {

//    @Autowired
    private TransferService transferService;

//    @Value("${application.commission}")
    private double commission = 0.01;

    @MockBean
    private OperationIdProvider operationIdMock;

    @MockBean
    private VerificationProvider verificationMock;

    @MockBean
    private TransferSuitableRepository repositoryMock;

    @BeforeEach
    void setUp() {
        transferService = new TransferService(operationIdMock, verificationMock, repositoryMock);
    }

    @Test
    void bidTransferRequest() {
        when(repositoryMock.getContactData(any())).thenReturn("contact");
        when(operationIdMock.serveAnOperationId()).thenReturn("0");

        OperationIdDto expectedOIDto = new OperationIdDto("0");
        OperationIdDto actualOIDto = transferService.bidTransferRequest(TRANSFER_1);

//        verify(repositoryMock).checkTransferPossibility(TRANSFER_1, commission);
        verify(verificationMock).performVerificationProcedure(TRANSFER_1, "contact");
        verify(operationIdMock).serveAnOperationId();
        assertEquals(expectedOIDto, actualOIDto);
    }

    @Test
    void confirmation_failsIf_noOperation() {
        ConfirmationDto confirmationDto = new ConfirmationDto("0", "0000");

        assertThrows(IllegalStateException.class,
                () -> transferService.commitTransferRequest(confirmationDto));

        verify(verificationMock, never()).isValidCodeForOperation(any(), any());
        verify(repositoryMock, never()).commitTransfer(any(), anyDouble());
    }
}