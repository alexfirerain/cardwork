package ru.netology.cardwork.exception;

public class FundsInsufficientException extends TransferNotPossibleException {
    public FundsInsufficientException(String s) {
        super(s);
    }
}
