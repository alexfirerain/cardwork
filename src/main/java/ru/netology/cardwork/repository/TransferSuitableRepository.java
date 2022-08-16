package ru.netology.cardwork.repository;

import ru.netology.cardwork.dto.Transfer;
import ru.netology.cardwork.exception.CardDataNotValidException;
import ru.netology.cardwork.exception.CardNotFoundException;
import ru.netology.cardwork.exception.TransferNotPossibleException;
import ru.netology.cardwork.model.Card;

/**
 * Contains abstract functionality for a repository capable of transfer operations
 * (as defined in the MoneyTransferServiceSpecification).
 * It implies that data are stored as accounts that are accessible via
 * associated cards and hold contact data to get in touch with its owner.
 * An implementation of this interface must be able to check possibility
 * of an offered transfer and able to commit it.
 * Also it will report of owner's contact data and the status of account in particular currency.
 */
public interface TransferSuitableRepository {

    /**
     * Checks whether offered properties in the transaction request meet the criteria.
     * @param request a Transfer object to be checked.
     */
    void checkTransferPossibility(Transfer request) throws CardNotFoundException, CardDataNotValidException, TransferNotPossibleException;

    /**
     * Executes a given transfer operation: subtracts a needed amount from a-account and adds it up to b-account
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
     * Reports contact data to get in touch with the card's owner.
     * @param card a card whose owner's address is queried.
     * @return a string containing owner's contact data.
     */
    String getContactData(Card card);

    /**
     * Tells how many funds is present at the card's account in given currency.
     * @param card     a card on which amount gets requested.
     * @param currency a currency in which amount gets requested.
     * @return  amount of funds available on given card in given currency.
     * @throws CardNotFoundException    if there's no card with such a number in the repository.
     * @throws CardDataNotValidException    if any of fields of given card doesn't match the entity in the repository.
     */
    int howManyFundsHas(Card card, String currency) throws CardNotFoundException, CardDataNotValidException;

}
