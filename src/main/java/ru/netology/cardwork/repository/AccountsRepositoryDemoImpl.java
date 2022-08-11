package ru.netology.cardwork.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.netology.cardwork.dto.Transfer;
import ru.netology.cardwork.exception.CardDataNotValidException;
import ru.netology.cardwork.exception.CardNotFoundException;
import ru.netology.cardwork.exception.FundsInsufficientException;
import ru.netology.cardwork.exception.TransferNotPossibleException;
import ru.netology.cardwork.model.Account;
import ru.netology.cardwork.model.Card;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A mock implementation of financial repository capable of transfer operations.
 */
@Repository
@Slf4j
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
        newRecord.addCurrencySubaccount("RUB", 0);
        accounts.put(cardAdding.getCardNumber(), newRecord);
    }

    /**
     * Tells whether repository contains an account associated with such a number.
     * @param number a number to be checked.
     * @return {@code true} if such a number is present and vice versa.
     */
    @Override
    public boolean containsCardNumber(String number) {
        return accounts.containsKey(number);
    }

    /**
     * Reports if the card with given number is capable of operations in given currency.
     * @param cardNumber a number of card in question.
     * @param currency   a name of currency in question.
     * @return {@code true} if an account at this number is present, active and has ability for this currency.
     */
    @Override
    public boolean isReadyForTransfer(String cardNumber, String currency) {
        Account account = accounts.get(cardNumber);
            return account != null
                    && account.isActive()
                    && account.hasCurrencyAccount(currency);
    }

    /**
     * Compares the given card's data with data of the card with its number in repository.
     * @param card a card's data to be verified.
     * @return {@code true} if every field of card taken and card kept in repository coincide.
     * @throws CardNotFoundException if a card with given number is absent from repository at all.
     */
    @Override
    public boolean isValidCardData(Card card) throws CardNotFoundException {
        String cardRequestedNumber = card.getCardNumber();
        Card cardInQuestion = getCardByNumber(cardRequestedNumber);
        if (cardInQuestion == null) {
            log.error("No card #{} found", cardRequestedNumber);
            throw new CardNotFoundException("Сведений о карте №" + cardRequestedNumber + " нет.");
        }
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

        if (!account.hasCurrencyAccount(currency)) {
            log.error("Attempt to access unexisting currency account at card#{}", card.getCardNumber());
            throw new IllegalArgumentException("На карте №" + card.getCardNumber() + " отсутствует " + currency + "-счёт.");
        }

        return account.fundsOnAccount(currency);
    }


    @Override
    public void commitTransfer(Transfer transferToCommit) {
        checkTransferPossibility(transferToCommit);

        // transaction imitation
        Card donorCard = transferToCommit.getCardFrom();
        Card acceptorCard = getCardByNumber(transferToCommit.getCardTo());
        int amount = transferToCommit.getTransferAmount().getValue();
        String currency = transferToCommit.getTransferAmount().getCurrency();
        Account donorAccount = getAccountByCard(donorCard);
        Account acceptorAccount = getAccountByCard(acceptorCard);

        int donorValue = howManyFundsHas(donorCard, currency);
        int acceptorValue = howManyFundsHas(acceptorCard, currency);

        donorAccount.subtractFunds(currency, amount);
        acceptorAccount.addFunds(currency, amount);

        if (donorValue != howManyFundsHas(donorCard, currency) + amount ||
            acceptorValue != howManyFundsHas(acceptorCard, currency) - amount)
        {
            donorAccount.getCurrencySubaccounts().put(currency, donorValue);
            acceptorAccount.getCurrencySubaccounts().put(currency, acceptorValue);
            log.error("Transfer {} not committed", transferToCommit);
            throw new IllegalStateException("Перевод отменён из-за сбоя системы.");
        }
        log.info("Transfer {} committed", transferToCommit);
    }

    @Override
    public String getContactData(Card card) {
        validateCard(card);
        return getAccountByCard(card).getContactData();
    }

    /**
     * Validates possibility of such a transfer operation, checking if both cards are present,
     * active and capable for required currency, if data of donor card are valid against the corresponding
     * in the repository, and if this card holds enough funds to commit this transfer.
     * If any of conditions is not met, throws an exception.
     * @param request a Transfer object to be checked.
     */
    @Override
    public void checkTransferPossibility(Transfer request) {
        Card donorCard = request.getCardFrom();
        String currency = request.getTransferAmount().getCurrency();
        validateCard(donorCard);
        if (!isReadyForTransfer(donorCard.getCardNumber(), currency) ||
             !isReadyForTransfer(request.getCardTo(), currency)) {
            log.error("Some of the cards is not capable of such a transfer");
            throw new TransferNotPossibleException("Перевод не может быть осуществлён");
        }

        if(request.getTransferAmount().getValue() > howManyFundsHas(donorCard, currency)) {
            log.error("There's not enough funds at the card#{} for such a transfer", donorCard.getCardNumber());
            throw new FundsInsufficientException("Не достаточно средств для осуществления перевода");
        }
    }

    private void validateCard(Card card) throws CardNotFoundException, CardDataNotValidException {
        String cardNumber = card.getCardNumber();

        if (!containsCardNumber(cardNumber)) {
            log.error("Card #{} not found", cardNumber);
            throw new CardNotFoundException("не найдено карты с  №" + cardNumber);
        }

        if (!isValidCardData(card)) {
            log.error("Card data are invalid");
            throw new CardDataNotValidException("Данные карты №" + cardNumber + " не соответствуют. К сожалению.");
        }

    }

}
