package ru.netology.cardwork.providers.verification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.netology.cardwork.model.Transfer;

import javax.validation.constraints.NotBlank;

/**
 * A simple implementation of the Verification Provider.
 * It holds a known string constant and reports OK
 * when the code to be verified coincides with it.
 */
@Component
@Slf4j
public class VerificationProviderDemoImpl implements VerificationProvider {

    /**
     * This implementation always supplies the same code, defined by this constant.
     */
    @NotBlank(message = "код подтверждения не может быть пуст")
    public static final String CONSTANT = "0000";

    /**
     * This implementation does nothing, just implying generating of a new code and sending it to the address somehow.
     * @param request             a transfer request which the code is generated for (plays no role in the impl).
     * @param confirmationAddress an address which the verification is meant to be sent on (plays no role in the impl).
     */
    @Override
    public void performVerificationProcedure(Transfer request, String confirmationAddress) {
        log.info("verification process simulated");
    }

    /**
     * Reports if received code coincides with the constant or not.
     * @param dealToCommit a pending operation waiting to be confirmed (plays no role in the impl).
     * @param codeReceived a given code to be compared.
     * @return whether the code is right.
     */
    @Override
    public boolean isValidCodeForOperation(Transfer dealToCommit,
                                           @NotBlank(message = "код подтверждения пуст") String codeReceived) {
        return CONSTANT.equals(codeReceived);
    }
}
