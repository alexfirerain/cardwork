package ru.netology.cardwork.providers.verification;

import org.springframework.stereotype.Component;
import ru.netology.cardwork.dto.Transfer;

import javax.validation.constraints.NotBlank;

@Component
public class VerificationProviderDemoImpl implements VerificationProvider {

    @NotBlank(message = "код подтверждения не может быть пуст")
    public static final String CONSTANT = "0000";

    /**
     * This implementation does nothing, just implying generating of a new code and sending it to the address somehow.
     * @param request             a transfer request which the code gets generated for.
     * @param confirmationAddress an address which the verification is to be sent on.
     */
    @Override
    public void provideAVerificationCodeFor(Transfer request, String confirmationAddress) {
    }

    @Override
    public boolean accepts(Transfer dealToCommit, @NotBlank(message = "код подтверждения пуст")
                               String codeReceived) {
        return CONSTANT.equals(codeReceived);
    }
}
