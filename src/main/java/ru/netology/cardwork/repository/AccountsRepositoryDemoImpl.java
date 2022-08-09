package ru.netology.cardwork.repository;

import org.springframework.stereotype.Repository;
import ru.netology.cardwork.dto.Transfer;
import ru.netology.cardwork.exception.CardDataNotValidException;
import ru.netology.cardwork.exception.CardNotFoundException;
import ru.netology.cardwork.exception.TransferNotPossibleException;
import ru.netology.cardwork.model.Account;
import ru.netology.cardwork.model.Card;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A mock implementation of financial repository capable of transfer operations.
 */
@Repository
public class AccountsRepositoryDemoImpl implements AccountsRepository {
    /**
     * The implementation of banking structure holding a card number string as a key
     * and an Account object as a value.
     */
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();


/*
    Methods managing Entities.
 */

    /**
     * Puts a Card object to the repository as a new active Account, assuming contactData
     * to be an empty string. Assigns to it a new empty account in "RUB" currency.
     * @param cardAdding a card to be inserted into the base.
     */
    public void addDefaultAccount(Card cardAdding) {
        Account newRecord = new Account(cardAdding);
        newRecord.addAccount("RUB", 0);
        accounts.put(cardAdding.getCardNumber(), newRecord);
    }


    @Override
    public boolean containsCardNumber(String number) {
        return accounts.containsKey(number);
    }

    @Override
    public boolean isReadyForTransfer(String cardNumber, String currency) {
        Account account = accounts.get(cardNumber);
            return account != null
                    && account.isActive()
                    && account.hasCurrencyAccount(currency);
    }

    @Override
    public boolean isValidCardData(Card card) throws CardNotFoundException {
        String cardRequestedNumber = card.getCardNumber();
        Card cardInQuestion = getCardByNumber(cardRequestedNumber);
        if (cardInQuestion == null)
            throw new CardNotFoundException("Сведений о карте №" + cardRequestedNumber + " нет.");
        return cardInQuestion.equals(card);
    }

    /**
     * Retrieves from the repository a Card object which has given number.
     * @param number a card number in question.
     * @return a Card object with number in question; {@code null} if such a card is absent.
     */
    private Card getCardByNumber(String number) {
        return accounts.get(number).getCardEntity();
    }

    /**
     * Retrieves from the repository an Account object
     * corresponding to the given Card.
     * @param card card identities in question.
     * @return a fully qualified Account entity, {@code null} if there's no object with such identities in da base.
     */
    private Account getAccountByCard(Card card) {
        validateCard(card);
        return accounts.get(card.getCardNumber());
    }

    /**
     * Reports funds available on given account at given card.
     * @param card     a card on which amount gets requested.
     * @param currency a currency in which amount gets requested.
     * @return state of the account in given currency, {@code 0} if account in given currency is absent at this card.
     * @throws CardNotFoundException    if there's no such a card in the repository.
     * @throws CardDataNotValidException    if any of card data is not the same as in one in the repository.
     */
    @Override
    public int howManyFundsHas(Card card,
                               String currency) throws CardNotFoundException,
                                                       CardDataNotValidException,
                                                       TransferNotPossibleException {
        validateCard(card);

        Account account = getAccountByCard(card);

        if (!account.hasCurrencyAccount(currency))
            throw new TransferNotPossibleException("На карте №" + card.getCardNumber() + " отсутствует " + currency + "-счёт.");

        return account.fundsOnAccount(currency);
    }

    @Override
    public void commitTransfer(Transfer transferToCommit) {

    }

    @Override
    public String getContactData(Card card) {
        validateCard(card);
        return getAccountByCard(card).getContactData();
    }

    @Override
    public void checkTransferPossibility(Transfer request) {

    }

    private void validateCard(Card card) throws CardNotFoundException, CardDataNotValidException {
        String cardNumber = card.getCardNumber();

        if (!containsCardNumber(cardNumber))
            throw new CardNotFoundException("не найдено карты с  №" + cardNumber);

        if (!isValidCardData(card))
            throw new CardDataNotValidException("Данные карты №" + cardNumber + " не соответствуют. К сожалению.");

    }

}
