package ru.netology.cardwork.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

class CardTest {

    private final Card example = new Card("1234123412341234", "09/28", "758");

    @Test
    void expirationDate_correctlyReflects_asDate() {
        Date actual = example.getValidTill();
        Date expected = new GregorianCalendar(2028, Calendar.OCTOBER, 1).getTime();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void expirationDate_correctlyReflects_asString() {
        String actual = example.getValidTillString();
        String expected = "09/28";
        Assertions.assertEquals(expected, actual);
    }
}