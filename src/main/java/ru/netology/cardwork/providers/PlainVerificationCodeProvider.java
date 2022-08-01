package ru.netology.cardwork.providers;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Component
public class PlainVerificationCodeProvider implements VerificationCodeProvider{
    @Override
    public String provideAVerificationCode() {
        return "0000";
    }

    @Override
    public boolean accepts(@NotBlank String codeReceived) {
        return provideAVerificationCode().equals(codeReceived);
    }
}
