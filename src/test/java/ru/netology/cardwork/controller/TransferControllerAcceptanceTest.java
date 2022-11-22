package ru.netology.cardwork.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.cardwork.dto.OperationIdDto;
import ru.netology.cardwork.providers.id.OperationIdProviderDemoImpl;
import ru.netology.cardwork.providers.verification.VerificationProviderDemoImpl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.netology.cardwork.repository.DemoData.TRANSFER_1;

@SpringBootTest
@AutoConfigureMockMvc
class TransferControllerAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void acceptTransferRequest() throws Exception {

        // define which operationID is about to be set for the transfer to come
        String operationID = String.valueOf(OperationIdProviderDemoImpl.checkCurrentId());

        mockMvc.perform(
                        post("/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(TRANSFER_1.toJson()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationId").value(operationID))
                .andReturn();
    }


    @Test
    void confirmTransferRequest() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        // first we send a request for the transfer to be bid
        // and catch the response with its operationID
        String response
                = mockMvc.perform(
                            post("/transfer")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(TRANSFER_1.toJson())
                        )
                        .andDo(print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        OperationIdDto result = mapper.readValue(response, OperationIdDto.class);

        // then we send a confirmation with matching operationID
        // and assure that we receive a response with that very ID again
        mockMvc.perform(
                    post("/confirmOperation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content("{ \"operationId\" : \"%s\", \"code\" : \"%s\" }"
                                    .formatted(result.getOperationId(), VerificationProviderDemoImpl.provideACode()))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationId").value(result.getOperationId()))
                .andReturn();

    }
}