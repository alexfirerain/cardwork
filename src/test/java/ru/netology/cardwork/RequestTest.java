package ru.netology.cardwork;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.netology.cardwork.controller.TransferController;
import ru.netology.cardwork.dto.OperationIdDto;
import ru.netology.cardwork.dto.Transfer;
import ru.netology.cardwork.service.TransferService;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
//@WebFluxTest(TransferController.class)
public class RequestTest {
    @Autowired
    private TransferController transferController;

    @MockBean
    private TransferService service;
    @Test
    public void controller_present() {
        assertThat(transferController).isNotNull();
    }

    public void correct_request () {
        Transfer transfer = new Transfer("1234567812345678",
                "03/25",
                "125",
                "8765432187654321",
                5000,
                "RUR");
        OperationIdDto operationId = new OperationIdDto("0");
        Mockito.when(service.bidTransferRequest(transfer)).thenReturn(operationId);
    }
}
