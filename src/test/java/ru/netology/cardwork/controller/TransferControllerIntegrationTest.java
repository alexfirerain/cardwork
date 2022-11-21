package ru.netology.cardwork.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.cardwork.dto.ConfirmationDto;
import ru.netology.cardwork.dto.OperationIdDto;
import ru.netology.cardwork.providers.id.OperationIdProvider;
import ru.netology.cardwork.providers.verification.VerificationProviderDemoImpl;
import ru.netology.cardwork.service.TransferService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.netology.cardwork.controller.TransferControllerTest.TRANSFER_1;

@WebMvcTest
class TransferControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService transferService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void acceptTransferRequest() throws Exception {
        String operationID = "0";
        when(transferService.bidTransferRequest(TRANSFER_1))
                .thenReturn(new OperationIdDto(operationID));

        mockMvc.perform(
                    post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(TRANSFER_1.toJson()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationId").value(operationID))
                .andReturn();

        verify(transferService).bidTransferRequest(TRANSFER_1);
    }

    @Test
    void confirmTransferRequest() throws Exception {
        String operationID = "0";
        ConfirmationDto confirmation = new ConfirmationDto(operationID, VerificationProviderDemoImpl.provideACode());
        when(transferService.commitTransferRequest(confirmation))
                .thenReturn(new OperationIdDto(confirmation.getOperationId()));

        mockMvc.perform(
                        post("/confirmOperation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(confirmation)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationId").value(operationID))
                .andReturn();

        verify(transferService).commitTransferRequest(confirmation);
    }
}