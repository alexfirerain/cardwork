package ru.netology.cardwork.providers;

/**
 * Provides a verification code for a transfer to be approved.
 */
public interface VerificationCodeProvider {
    /**
     * Supplies a verification code for current operation
     * @return  a proper code.
     */
    String provideAVerificationCode();

    /**
     * Informs whether the code received matches a proper code the provider provides.
     * @param codeReceived  a given code to be compared.
     * @return  {@code true} if given code is true, {@code false} otherwise.
     */
    boolean accepts(String codeReceived);
}
