package ru.netology.cardwork.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.netology.cardwork.config.TransferControllerTestConfig;

import javax.servlet.ServletContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TransferControllerTestConfig.class })
@WebAppConfiguration
public class TransferControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

//    @Test
//    public void givenWac_whenServletContext_thenItProvidesTransferService() {
//        ServletContext servletContext = webApplicationContext.getServletContext();
//
//        Assert.assertNotNull(servletContext);
//        Assert.assertTrue(servletContext instanceof MockServletContext);
//        Assert.assertNotNull(webApplicationContext.getBean("transferService"));
//    }
//
//    @Test
//    public void givenTransferURIWithPost_whenMockMVC_thenVerifyResponse() throws Exception {
//        this.mockMvc.perform(post("/transfer")).andDo(print())
//                       .andExpect(status().isOk())
//                       .andExpect(content().contentType("application/json;charset=UTF-8"))
//                       .andExpect(jsonPath("$.operationId").value("0"));
//    }


}
