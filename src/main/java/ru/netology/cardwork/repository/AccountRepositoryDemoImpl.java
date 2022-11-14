package ru.netology.cardwork.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.netology.cardwork.model.Transfer;
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

    /**
     * A bunch of special currency subaccounts that accumulate the commission fees
     * on transfers that charged in favor of bank.
     * The given implementation has no tools to access this funds,
     * they just get accumulated here and stored.
     */
    private final Map<String, Integer> commissionAccount = new ConcurrentHashMap<>();

    /**
     * Creates a new demo account repository
     * and uploads in it the data defined in the DemoData class.
     */
    public AccountRepositoryDemoImpl() {
        // TODO: как это кошернее всего реализовать для тестового профиля?
        addAccounts(DemoData.ALL_ACCOUNTS);
    }

/*
    Methods managing Entities.
 */

    /**
     * Puts a Card object to the repository as a new active Account, assuming contactData
     * to be an empty string. Assigns to it a new empty account in "RUR" currency.
     * @param cardAdding a card to be inserted into the base.
     */
    @Override
    public void addDefaultAccount(Card cardAdding) {
        addAccount(
                (new Account(cardAdding))
                        .addCurrencySubaccount("RUR", 0)
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
        log.info("Have account written to the base: {}", account);
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
    private int howManyFundsHas(Card card,
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
     * Validates possibility of such a transfer operation, checking if the source card and target card are
     * different and both cards are present, active and capable for required currency,
     * if data of donor card are valid against the corresponding in the repository,
     * and if this card holds enough funds to commit this transfer.
     * If any of conditions is not met, throws an exception with a description of the problem.
     * @param request a Transfer object to be checked.
     * @throws CardNotFoundException    if the donor card is absent in repository.
     * @throws CardDataNotValidException    if validation of the donor card fails.
     * @throws TransferNotPossibleException    if the recipient card is absent,
     *  or any of cards is not active or does not have a subaccount in required currency,
     *  also if source and target cards coinside.
     * @throws FundsInsufficientException   if the donor card doesn't possess
     * enough funds to commit the transaction (counting commission in).
     */
    @Override
    public void checkTransferPossibility(Transfer request, double commissionRate) throws CardNotFoundException,
                                                                  CardDataNotValidException,
                                                                  TransferNotPossibleException {
        Card donorCard = request.getCardFrom();
        validateCard(donorCard);

        String donorNumber = donorCard.getCardNumber();
        String recipientNumber = request.getCardTo();
        if (donorNumber.equals(recipientNumber)) {
            log.warn("Original and terminal cards should differ.");
            throw new TransferNotPossibleException("Карта списания и получателя не может совпадать");
        }

        String currency = request.getTransferAmount().getCurrency();
        String whyNot = whyTransferImpossible(donorNumber, currency);
        if (whyNot == null) {
            whyNot = whyTransferImpossible(recipientNumber, currency);
        }
        if (whyNot != null) {
            log.warn("Some of the cards is not capable of such a transfer: {}", whyNot);
            throw new TransferNotPossibleException("Перевод невозможен: " + whyNot);
        }

        int sumRequired = (int) Math.round(request.getTransferAmount().getValue() * (1 + commissionRate));
        if(sumRequired > howManyFundsHas(donorCard, currency)) {
            log.warn("There's not enough funds at the card #{} for such a transfer", donorNumber);
            throw new FundsInsufficientException("Не достаточно средств.");
        }


    }

    /**
     * Commits the requested transferring of funds between accounts regarding the specified commission rate.
     * Simulates transactionality by checking that new state of accounts is as expected, rolling it back otherwise.
     * If something went wrong, throws a problem description.
     * @param transferToCommit a model of the operation to be performed.
     * @param commissionRate a rate of commission to be charged in favor of bank.
     * @throws CardNotFoundException    if the donor card is absent in repository.
     * @throws CardDataNotValidException    if validation of the donor card fails.
     * @throws TransferNotPossibleException    if the recipient card is absent,
     *  or any of cards is not active or does not have a subaccount in required currency,
     *  also if source and target cards coinside.
     * @throws FundsInsufficientException   if the donor card doesn't possess
     * enough funds to commit the transaction (counting commission in).
     * @throws IllegalStateException    if the transaction was rolled back for some reason.
     */
    @Override
    public void commitTransfer(Transfer transferToCommit, double commissionRate) {
        checkTransferPossibility(transferToCommit, commissionRate);

        // transaction imitation
        Card donorCard = transferToCommit.getCardFrom();
        Account donorAccount = getAccountByCard(donorCard);
        Card acceptorCard = getCardByNumber(transferToCommit.getCardTo());
        Account acceptorAccount = getAccountByCard(acceptorCard);
        String currency = transferToCommit.getTransferAmount().getCurrency();

        int amount = transferToCommit.getTransferAmount().getValue();
        int commissionAmount = (int) Math.round(amount * commissionRate);

        int oldDonorValue = howManyFundsHas(donorCard, currency);
        int oldAcceptorValue = howManyFundsHas(acceptorCard, currency);
        int oldCommissionAccountValue = commissionAccount.get(currency) == null ?
                                            0 : commissionAccount.get(currency);

        donorAccount.subtractFunds(currency, amount + commissionAmount);
        acceptorAccount.addFunds(currency, amount);
        commissionAccount.merge(currency, commissionAmount, Integer::sum);

        if (oldDonorValue - amount - commissionAmount != howManyFundsHas(donorCard, currency) ||
                oldAcceptorValue + amount != howManyFundsHas(acceptorCard, currency) ||
                oldCommissionAccountValue + commissionAmount != commissionAccount.get(currency))
        {
            donorAccount.getCurrencySubaccounts().put(currency, oldDonorValue);
            acceptorAccount.getCurrencySubaccounts().put(currency, oldAcceptorValue);
            commissionAccount.put(currency, oldCommissionAccountValue);
            log.error("Transfer {} not committed", transferToCommit);
            throw new IllegalStateException("Перевод отменён из-за сбоя системы.");
        }

        log.info("{} committed (commission: {})",
                                    transferToCommit,
                                    "%.2f %s".formatted(commissionAmount / 100., currency));
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
     * @return a string describing the problem which makes the transfer impossible.
     */
    private String whyTransferImpossible(String cardNumber, String currency) {
        Account account = accounts.get(cardNumber);
        if (account == null)
            return "Карты №%s не найдено.".formatted(cardNumber);
        if (!account.isActive())
            return "Счёт карты №%s не активен.".formatted(cardNumber);
        if (account.noSuchCurrency(currency))
            return "Счёт в %s отсутствует на карте №%s".formatted(currency, cardNumber);
        return null;
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
            throw new CardNotFoundException("не найдено карты №" + cardNumber);
        }

        if (!card.equals(getCardByNumber(cardNumber))) {
            log.error("Card #{} data are invalid", cardNumber);
            throw new CardDataNotValidException("Данные карты №" + cardNumber + " не соответствуют. К сожалению.");
        }

    }

    @Override
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
