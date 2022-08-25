package ru.netology.cardwork;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.netology.cardwork.controller.TransferController;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RequestTest {
    @Autowired
    private TransferController transferController;
    @Test
    public void controller_present() throws Exception {
        assertThat(transferController).isNotNull();
    }
}
