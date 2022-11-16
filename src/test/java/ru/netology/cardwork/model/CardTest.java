package ru.netology.cardwork.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    private final Card card = new Card("1234123412341234", "09/28", "758");

    @BeforeEach
    void setUp() {
    }

    @Test
    void date_correctlyReflects_asString() {
        String actual = card.getValidTillString();
        String expected = "09/28";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void date_correctlyReflects_asDate() {
        Date actual = card.getValidTill();
        Date expected = new GregorianCalendar(2028, Calendar.SEPTEMBER, 1).getTime();
        Assertions.assertEquals(expected, actual);
    }
}