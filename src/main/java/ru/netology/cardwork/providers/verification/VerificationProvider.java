package ru.netology.cardwork.providers.verification;

import ru.netology.cardwork.model.Transfer;
import ru.netology.cardwork.exception.VerificationFailureException;

/**
 * Provides a verification code for a transfer to be approved.
 */
public interface VerificationProvider {
    /**
     * Performs generation of a new validation code for the transfer operation and contacts
     * user via some method on the address provided.
     * @param request a transfer operation to be confirmed.
     * @param confirmationAddress an address the user is to be contacted on.
     * @throws VerificationFailureException if contacting user is impossible for some reason.
     */
    void performVerificationProcedure(Transfer request, String confirmationAddress) throws VerificationFailureException;

    /**
     * Informs whether the code received matches a proper code the provider provided for that transfer.
     *
     * @param dealToCommit a pending operation waiting to be confirmed.
     * @param codeReceived a given code to be compared.
     * @return {@code true} if given code is true, {@code false} otherwise.
     */
    boolean isValidCodeForOperation(Transfer dealToCommit, String codeReceived);

}
