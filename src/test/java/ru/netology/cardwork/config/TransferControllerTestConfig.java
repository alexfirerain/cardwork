package ru.netology.cardwork.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.netology.cardwork.service.TransferService;

@Configuration
public class TransferControllerTestConfig {

    @Bean
    public TransferService transferService()
    {
        return Mockito.mock(TransferService.class);
    }

}
