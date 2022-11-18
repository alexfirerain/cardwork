package ru.netology.cardwork.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.cardwork.dto.OperationIdDto;
import ru.netology.cardwork.service.TransferService;

import static org.hamcrest.Matchers.hasSize;
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

    @Test
    void acceptTransferRequest() throws Exception {
        when(transferService.bidTransferRequest(TRANSFER_1))
                .thenReturn(new OperationIdDto("0"));

        mockMvc.perform(
                    post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(TRANSFER_1.toJson()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['operationId']").value("0"))
//                .andExpect(jsonPath("$[0]", hasSize(1)))
                .andReturn().getResponse().getContentAsString()
        ;
    }

    @Test
    void confirmTransferRequest() {
    }
}