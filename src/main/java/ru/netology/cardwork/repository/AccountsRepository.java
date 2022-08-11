package ru.netology.cardwork.repository;

import ru.netology.cardwork.dto.Transfer;
import ru.netology.cardwork.exception.CardDataNotValidException;
import ru.netology.cardwork.exception.CardNotFoundException;
import ru.netology.cardwork.exception.TransferNotPossibleException;
import ru.netology.cardwork.model.Card;

public interface AccountsRepository {

    /**
     * executes a given transfer operation: subtracts a needed amount from a-account and adds it up to b-account
     * regarding appropriate commission policy.
     * @param transferToCommit a model of the operation to perform containing
     *                         all required data of sender's card,
     *                         a number of recipient's card and
     *                         a representation of the sum
     *                              including fund's amount and its currency.
     * @throws CardNotFoundException  if any of two involved cards' number not found in the database.
     * @throws CardDataNotValidException    if any of card's miscellaneous data doesn't match to the entity in the database.
     * @throws TransferNotPossibleException   if the required sum in given currency exceeds the rest at the sender's account
     *                                              or any of two involved cards is not active somehow
     *                                              or don't have subaccounts in currency required.
     */
    void commitTransfer(Transfer transferToCommit) throws CardNotFoundException, CardDataNotValidException, TransferNotPossibleException;

    /**
     * Tells whether  a card with a given number is present in the repository.
     * @param number a number to be checked.
     * @return {@code true} if there's a card with a given number present.
     */
    boolean containsCardNumber(String number);

    /**
     * Tells whether a card with such a number is ready for a transaction in given currency.
     * This means that there's a card with such number in the repository,
     * it is active and do have an account in currency in question.
     * None misc card data are checked.
     * @param cardNumber a number of card in question.
     * @param currency   a name of currency in question.
     * @return {@code true} if the number is present and corresponding card is active
     * and has an appropriate account. {@code false} otherwise.
     */
    boolean isReadyForTransfer(String cardNumber, String currency);

    /**
     * Tells whether card's data correspond to those in the repository.
     * @param card a card's data to be verified.
     * @return {@code true} if all fields of the card in question match the entity in the repository,
     * {@code false} otherwise.
     * @throws CardNotFoundException if a card with such a number is absent from the repository at all.
     */
    boolean isValidCardData(Card card) throws CardNotFoundException;

    /**
     * Tells how many funds is present at the card's account in given currency.
     * @param card     a card on which amount gets requested.
     * @param currency a currency in which amount gets requested.
     * @return  amount of funds available on given card in given currency.
     * @throws CardNotFoundException    if there's no card with such a number in the repository.
     * @throws CardDataNotValidException    if any of fields of given card doesn't match the entity in the repository.
     */
    int howManyFundsHas(Card card, String currency) throws CardNotFoundException, CardDataNotValidException;

    /**
     * Reports contact data to get in touch with the card's owner.
     * @param card a card whose owner's address is queried.
     * @return a string containing owner's contact data.
     */
    String getContactData(Card card);

    /**
     * Checks whether offered properties in the transaction request meet the criteria.
     * @param request a Transfer object to be checked.
     */
    void checkTransferPossibility(Transfer request);

}
