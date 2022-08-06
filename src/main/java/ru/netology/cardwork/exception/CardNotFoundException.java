package ru.netology.cardwork.exception;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(String s) {
        super(s);
    }
}
