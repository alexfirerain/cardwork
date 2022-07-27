package ru.netology.cardwork.repository;

import ru.netology.cardwork.dto.Transfer;
import ru.netology.cardwork.exception.CardDataNotValidException;
import ru.netology.cardwork.exception.FundsInsufficientException;
import ru.netology.cardwork.exception.TargetCardNotFoundException;

public interface AccountsRepository {

    /**
     * Executes a given transfer operation: subtracts needed amount from A account and adds it up to B account.
     * @param transferToCommit a description of the operation to perform containing
     *                         all required data of sender's card,
     *                         a number of recipient's card and
     *                         a representation of the sum
     *                              including fund's amount and its currency.
     * @throws CardDataNotValidException    if sender's card's number not found
     *                                      or any of its miscellaneous data doesn't match to the entity in the database.
     * @throws TargetCardNotFoundException  if recipient's card's number not found in the database.
     * @throws FundsInsufficientException   if the requested sum to transfer in given currency exceeds the rest at the sender's account.
     */
    void commitTransfer(Transfer transferToCommit) throws CardDataNotValidException, TargetCardNotFoundException, FundsInsufficientException;
}
