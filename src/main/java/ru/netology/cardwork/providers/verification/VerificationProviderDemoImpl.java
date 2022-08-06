package ru.netology.cardwork.providers.verification;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Component
public class VerificationProviderDemoImpl implements VerificationProvider {

    @NotBlank(message = "код подтверждения не может быть пуст")
    public static final String CONSTANT = "0000";

    @Override
    public String provideAVerificationCode() {
        return CONSTANT;
    }

    @Override
    public boolean accepts(@NotBlank(message = "код подтверждения пуст")
                               String codeReceived) {
        return provideAVerificationCode().equals(codeReceived);
    }
}
