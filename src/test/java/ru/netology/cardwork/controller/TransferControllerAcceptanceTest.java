package ru.netology.cardwork.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.cardwork.dto.OperationIdDto;
import ru.netology.cardwork.providers.verification.VerificationProviderDemoImpl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.netology.cardwork.controller.TransferControllerTest.TRANSFER_1;

@SpringBootTest
@AutoConfigureMockMvc
class TransferControllerAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    // TODO: these tests should be independent of order of invocation,
    //  so we need to invent a way to make them separate


    @Test
    void acceptTransferRequest() throws Exception {

        mockMvc.perform(
                        post("/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(TRANSFER_1.toJson()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationId").value("0"))
                .andReturn();
    }


    @Test
    void confirmTransferRequest() throws Exception {

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

        ObjectMapper mapper = new ObjectMapper();
        OperationIdDto result = mapper.readValue(response, OperationIdDto.class);

        mockMvc.perform(
                    post("/confirmOperation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content("{ \"operationId\" : \"%s\", \"code\" : \"%s\" }"
                                    .formatted(result.getOperationId(), VerificationProviderDemoImpl.CONSTANT))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationId").value(result.getOperationId()))
                .andReturn();

    }
}