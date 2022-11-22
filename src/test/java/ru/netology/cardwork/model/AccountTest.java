package ru.netology.cardwork.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.cardwork.repository.DemoData.CARD_1;
import static ru.netology.cardwork.repository.DemoData.CARD_2;

class AccountTest {

    private Account account;

    @BeforeEach
    void set_up() {
        account = new Account(CARD_1, "contact", true);
        account.addCurrencySubaccount("RUR", 100_000_00);
    }

    @Test
    void getCardNumber_test() {
        assertEquals(CARD_1.getCardNumber(), account.getCardNumber());
    }

    @Test
    void getCardValidTill_test() {
        assertEquals(CARD_1.getValidTill(), account.getCardValidTill());
    }

    @Test
    void getCardCVV_test() {
        assertEquals(CARD_1.getCardCVV(), account.getCardCVV());
    }

    @Test
    void noSuchCurrency_test() {
        assertTrue(account.noSuchCurrency("USD"));
    }

    @Test
    void fundsOnAccount_test() {
        assertEquals(100_000_00, account.fundsOnAccount("RUR"));
    }

    @Test
    void addCurrencySubaccount_test() {
        account.addCurrencySubaccount("USD");

        assertFalse(account.noSuchCurrency("USD"));
    }

    @Test
    void addFunds_test() {
        account.addFunds("RUR", 80_000_00);

        assertEquals(180_000_00, account.fundsOnAccount("RUR"));
    }

    @Test
    void addFunds_exception_test() {
        assertThrows(IllegalArgumentException.class,
                () -> account.addFunds("USD", 80_000_00));
    }

    @Test
    void subtractFunds_test() {
        account.subtractFunds("RUR", 30_000_00);

        assertEquals(70_000_00, account.fundsOnAccount("RUR"));
    }

    @Test
    void subtractFunds_exception_test() {
        assertThrows(IllegalArgumentException.class,
                () -> account.subtractFunds("USD", 30_000_00));
    }

    @Test
    void setCardEntity_test() {
        account.setCardEntity(CARD_2);

        assertEquals(CARD_2.getCardNumber(), account.getCardNumber());
    }
}