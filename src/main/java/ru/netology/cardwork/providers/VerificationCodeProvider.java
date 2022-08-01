package ru.netology.cardwork.providers;

public interface VerificationCodeProvider {
    String provideAVerificationCode();

    boolean accepts(String codeReceived);
}
