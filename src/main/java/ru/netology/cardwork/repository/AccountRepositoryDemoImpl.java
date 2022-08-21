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

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A mock implementation of financial repository capable of transfer operations.
 */
@Repository
@Slf4j
public class AccountRepositoryDemoImpl implements TransferSuitableRepository,
                                                  ManageableAccountRepository {
    /**
     * The implementation of banking structure holding a card number string as a key
     * and an Account object as a value.
     */
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    private final Map<String, Double> commissionAccount = new ConcurrentHashMap<>();

    public AccountRepositoryDemoImpl() {
        // TODO: как это кошернее всего реализовать для тестового профиля?
        addAccounts(DemoData.ALL_ACCOUNTS);
    }

/*
    Methods managing Entities.
 */

    /**
     * Puts a Card object to the repository as a new active Account, assuming contactData
     * to be an empty string. Assigns to it a new empty account in "RUB" currency.
     * @param cardAdding a card to be inserted into the base.
     */
    @Override
    public void addDefaultAccount(Card cardAdding) {
        addAccount(
                (new Account(cardAdding))
                        .addCurrencySubaccount("RUB", 0.)
        );
    }

    /**
     * Just adds a new ready account to the repository.
     * @param account which is added.
     */
    @Override
    public void addAccount(Account account) {
        String cardNumber = account.getCardNumber();
        if (accounts.containsKey(cardNumber)) {
            log.warn("Card #{} already in da base, will be rewritten", cardNumber);
        }
        accounts.put(account.getCardNumber(), account);
        log.info("Have account @cardNumber {} written to the base", cardNumber);
    }

    @Override
    public void addAccounts(Account[] accounts) {
        Arrays.stream(accounts).forEach(this::addAccount);
    }

    @Override
    public void resetAccounts() {
        accounts.clear();
    }

    @Override
    public void deleteAccount(Card card) {
        accounts.remove(card.getCardNumber());
    }

    @Override
    public void reassignAccountToCard(Account oldAccount, Card newCard) {
        Account changingAccount = getAccountByCard(oldAccount.getCardEntity());
        changingAccount.setCardEntity(newCard);
        addAccount(changingAccount);
        if (!changingAccount.getCardNumber().equals(oldAccount.getCardNumber())) {
            accounts.remove(oldAccount.getCardNumber());
        }
    }


    /**
     * Reports funds available on given account at given card.
     *
     * @param card     a card on which amount gets requested.
     * @param currency a currency in which amount gets requested.
     * @return state of the account in given currency, {@code 0} if account in given currency is absent at this card.
     * @throws CardNotFoundException     if there's no such a card in the repository.
     * @throws CardDataNotValidException if any of card data is not the same as in one in the repository.
     */
    private double howManyFundsHas(Card card,
                                   String currency) throws CardNotFoundException,
                                                           CardDataNotValidException,
                                                           IllegalArgumentException {
        Account account = getAccountByCard(card);

        if (account.noSuchCurrency(currency)) {
            log.error("Attempt to access unexisting currency {} account at card#{}", currency, card.getCardNumber());
            throw new IllegalArgumentException("На карте №%s отсутствует %s-счёт."
                    .formatted(card.getCardNumber(), currency));
        }

        return account.fundsOnAccount(currency);
    }

    @Override
    public String getContactData(Card card) throws CardNotFoundException,
            CardDataNotValidException {
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
    public void checkTransferPossibility(Transfer request, double commissionRate) throws CardNotFoundException,
                                                                  CardDataNotValidException,
                                                                  TransferNotPossibleException {
        Card donorCard = request.getCardFrom();
        String currency = request.getTransferAmount().getCurrency();
        validateCard(donorCard);
        if (transferNotPossible(donorCard.getCardNumber(), currency) ||
                transferNotPossible(request.getCardTo(), currency)) {
            log.error("Some of the cards is not capable of such a transfer");
            throw new TransferNotPossibleException("Перевод не может быть осуществлён");
        }

        double sumRequired = roundToCents(request.getTransferAmount().getValue() * (1 + commissionRate));
        if(sumRequired > howManyFundsHas(donorCard, currency)) {
            log.error("There's not enough funds at the card#{} for such a transfer", donorCard.getCardNumber());
            throw new FundsInsufficientException("Не достаточно средств для осуществления перевода");
        }
    }

    @Override
    public void commitTransfer(Transfer transferToCommit, double commissionRate) {
        checkTransferPossibility(transferToCommit, commissionRate);

        // transaction imitation
        Card donorCard = transferToCommit.getCardFrom();
        Card acceptorCard = getCardByNumber(transferToCommit.getCardTo());
        double amount = (double) transferToCommit.getTransferAmount().getValue();
        double commission = roundToCents(amount * commissionRate);
        String currency = transferToCommit.getTransferAmount().getCurrency();
        Account donorAccount = getAccountByCard(donorCard);
        Account acceptorAccount = getAccountByCard(acceptorCard);

        double oldDonorValue = howManyFundsHas(donorCard, currency);
        double oldAcceptorValue = howManyFundsHas(acceptorCard, currency);
        double oldCommissionAccountValue = commissionAccount.get(currency) == null ?
                                            0 : commissionAccount.get(currency);

        donorAccount.subtractFunds(currency, amount + commission);
        acceptorAccount.addFunds(currency, amount);
        commissionAccount.merge(currency, commission, Double::sum);

        if (oldDonorValue - amount - commission != howManyFundsHas(donorCard, currency) ||
                oldAcceptorValue + amount != howManyFundsHas(acceptorCard, currency) ||
                oldCommissionAccountValue + commission != commissionAccount.get(currency))
        {
            donorAccount.getCurrencySubaccounts().put(currency, oldDonorValue);
            acceptorAccount.getCurrencySubaccounts().put(currency, oldAcceptorValue);
            commissionAccount.put(currency, oldCommissionAccountValue);
            log.error("Transfer {} not committed", transferToCommit);
            throw new IllegalStateException("Перевод отменён из-за сбоя системы.");
        }

        log.info("{} committed (commission: {})",
                                    transferToCommit,
                                    "%.2f %s".formatted(commission, currency));
        log.debug(reportAccountsState());
    }




    /*
        Auxiliary internal functions.
     */
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
     * If such a card is absent from the repository
     * or not equals in details to the one with the same number, exception throws.
     * @param card card identities in question.
     * @return a fully qualified Account entity, {@code null} if there's no object with such identities in da base.
     * @throws CardNotFoundException     if there's no card with such a number in the repository.
     * @throws CardDataNotValidException    if any of card's fields differ from the ones in the repository.
     */
    private Account getAccountByCard(Card card) {
        validateCard(card);
        return accounts.get(card.getCardNumber());
    }

    /**
     * Tells whether a card with such a number is capable of a transaction in given currency.
     * This means that there's a card with such number in the repository,
     * it is active and do have an account in currency in question.
     * None misc card data (whether is it expired etc.) are checked.
     * @param cardNumber a number of card in question.
     * @param currency   a name of currency in question.
     * @return {@code true} if the account at this number is present,
     * active and has ability for this currency. {@code false} otherwise.
     */
    private boolean transferNotPossible(String cardNumber, String currency) {
        Account account = accounts.get(cardNumber);
        return account == null
                || !account.isActive()
                || account.noSuchCurrency(currency);
    }

    /**
     * A procedure of validating a card: that it is present and equal to the entity in the repository.
     * @param card a card being validated.
     * @throws CardNotFoundException    if there's no such a card.
     * @throws CardDataNotValidException    if any of card's fields doesn't match.
     */
    private void validateCard(@Valid Card card) throws CardNotFoundException, CardDataNotValidException {
        String cardNumber = card.getCardNumber();

        if (!accounts.containsKey(cardNumber)) {
            log.error("Card #{} not found", cardNumber);
            throw new CardNotFoundException("не найдено карты с  №" + cardNumber);
        }

        if (!card.equals(getCardByNumber(cardNumber))) {
            log.error("Card #{} data are invalid", cardNumber);
            throw new CardDataNotValidException("Данные карты №" + cardNumber + " не соответствуют. К сожалению.");
        }

    }

    private double roundToCents(double value) {
        return Math.round(value * 100) / 100.;
    }

    public String reportAccountsState() {
        StringBuilder report = new StringBuilder("\n=== %s: ===\n".formatted(this.toString()));

        for (Account account : accounts.values()) {
            report.append("#%s%s:\n".formatted(account.getCardNumber(), account.isActive() ? "" : " (inactive)"));
            report.append(Account.listSubaccounts(account.getCurrencySubaccounts(), "-"));
        }

        report.append("commission account:\n%s"
                        .formatted(Account.listSubaccounts(commissionAccount, "-")));

        return report.toString();
    }

}