package ru.netology.cardwork.repository;

import ru.netology.cardwork.dto.Transfer;
import ru.netology.cardwork.exception.CardDataNotValidException;
import ru.netology.cardwork.exception.CardNotFoundException;
import ru.netology.cardwork.exception.TransferNotPossibleException;
import ru.netology.cardwork.model.CardIdentity;

public interface AccountsRepository {

    /**
     * Executes a given transfer operation: subtracts a needed amount from A-account and adds it up to B-account
     * regarding appropriate commission policy.
     * @param transferToCommit a model of the operation to perform containing
     *                         all required data of sender's card,
     *                         a number of recipient's card and
     *                         a representation of the sum
     *                              including fund's amount and its currency.
     * @throws CardNotFoundException  if any of two involved cards' number not found in the database.
     * @throws CardDataNotValidException    if any of card's miscellaneous data doesn't match to the entity in the database.
     * @throws TransferNotPossibleException   if the required sum in given currency exceeds the rest at the sender's account
     *                                              or any of two involved cards is not active somehow.
     */
    void commitTransfer(Transfer transferToCommit) throws CardNotFoundException, CardDataNotValidException, TransferNotPossibleException;

    /**
     * Tells whether  a card with a given number is present in the repository.
     * @param number a number to be checked.
     * @return {@code true} if there's a card with a given number present.
     */
    boolean containsCardNumber(String number);

    /**
     * Tells whether card's data correspond to those in the repository.
     * @param cardIdentity a card's data to be verified.
     * @return {@code true} if all fields of the card in question match the entity in the repository.
     * @throws CardNotFoundException if a card with such a number is absent from the repository at all.
     */
    boolean isValidCardData(CardIdentity cardIdentity) throws CardNotFoundException;

    /**
     * Tells how many funds is present at the card's account in given currency.
     * @param card     a card on which amount gets requested.
     * @param currency a currency in which amount gets requested.
     * @return  amount of funds available on given card in given currency.
     * @throws CardNotFoundException    if there's no card with such a number in the repository.
     * @throws CardDataNotValidException    if any of fields of given card doesn't match the entity in the repository.
     */
    int howManyFundsHas(CardIdentity card, String currency) throws CardNotFoundException, CardDataNotValidException;

    String getContactData(CardIdentity card);
}
