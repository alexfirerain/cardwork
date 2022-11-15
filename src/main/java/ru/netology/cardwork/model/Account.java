package ru.netology.cardwork.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A data structure representing a money multiaccaunt.
 */
@Getter
@Setter
@AllArgsConstructor
@Slf4j
public class Account {
    /**
     * A card object this account is bound to.
     */
    @NotNull
    private Card cardEntity;
    /**
     * A string of contact information.
     */
    private String contactData;
    /**
     * Flag signalizing that the account is OK to go.
     */
    private boolean isActive;
    /**
     * The multiaccaunt holding currency naming as a key
     * and value in Integer as a value.
     * Using integer values implies that cent subunits are used (kopecks and alike).
     */
    private final Map<String, Integer> currencySubaccounts = new ConcurrentHashMap<>();

    /**
     * Creates a new active account with empty contact data.
     * @param cardAdding a Card object this account is bound to.
     */
    public Account(Card cardAdding) {
        this(cardAdding,  "", true);
        log.trace("Account constructed: {}", this);
    }

    public String getCardNumber() {
        return cardEntity.getCardNumber();
    }
    public Date getCardValidTill() {
        return cardEntity.getValidTill();
    }
    public String getCardCVV() {
        return cardEntity.getCardCVV();
    }

    /**
     * Tells whether this account is active or not.
     * @return {@code true} if it is active.
     */
    public boolean isActive() {
        return isActive;
    }
    /**
     * Sets this account active.
     * @return this activated account for chain usage.
     */
    public Account setActive() {
        isActive = true;
        return this;
    }
    /**
     * Sets this account inactive.
     * @return this disactivated account for chain usage.
     */
    public Account setInactive() {
        isActive = false;
        return this;
    }

    /**
     * Sets contact data for this account. If they already present, get replaced.
     * @param contactData contact data for the account.
     * @return this renewed account for chain usage.
     */
    public Account setContactData(String contactData) {
        this.contactData = contactData;
        return this;
    }

    /**
     * Tells whether this account does not or does have  a subaccount in given currency.
     * @param currency the currency in question.
     * @return {@code true} if there's no such a subaccount, {@code false} if such a subbaccount is present.
     */
    public boolean noSuchCurrency(String currency) {
        return !currencySubaccounts.containsKey(currency);
    }

    /**
     * Reports available funds on given subaccount.
     * @param currency a currency subaccount in question.
     * @return amount of funds in given currency available on the account.
     * @throws IllegalArgumentException if there's no subaccount that been requested.
     */
    public int fundsOnAccount(String currency) {
        if (noSuchCurrency(currency)) throwIAEofNoCurrency();

        return currencySubaccounts.get(currency);
    }

    /**
     * Adds a new empty subaccount in given currency.
     * If such subaccount already present, its old value gets erased.
     * @param currency a naming code of currency for the subaccount.
     * @return  this renewed account.
     */
    public Account addCurrencySubaccount(String currency) {
        currencySubaccounts.put(currency, 0);
        return this;
    }

    /**
     * Adds a new subaccount in given currency with given initial value.
     * If such subaccount already present, its old value gets lost.
     * @param currency a naming code of currency for the subaccount.
     * @return  this renewed account.
     */
    public Account addCurrencySubaccount(String currency, int value) {
        currencySubaccounts.put(currency, value);
        return this;
    }

    /**
     * Adds an offered value to the current value at specified subaccount.
     * @param currency a currency of the operation.
     * @param value    an amount to be added.
     * @return  a new value at the subaccount.
     * @throws IllegalArgumentException if there's no subaccount that been requested.
     */
    public double addFunds(String currency, int value) {
        if (noSuchCurrency(currency)) throwIAEofNoCurrency();

        return currencySubaccounts.merge(currency, value, Integer::sum);
    }

    /**
     * Subtracts an offered value from the current value at specified subaccount.
     * @param currency a currency of the operation.
     * @param value    an amount to be minused.
     * @return  a new value at the subaccount.
     * @throws IllegalArgumentException if there's no subaccount that been requested.
     */
    public double subtractFunds(String currency, int value) {
        // the cards and accounts here are ABLE of keeping negative values,
        // so no checking for them to be positive needed
        return addFunds(currency, -value);
    }

    /**
     * A shortcut to throw an IllegalStateArgumentException
     * telling this account has no required currency subaccount.
     * @throws IllegalArgumentException when invoked.
     */
    private void throwIAEofNoCurrency() {
        throw new IllegalArgumentException("no such currency @card#" + getCardNumber());
    }

    @Override
    public String toString() {
        return """
                
                Счёт:
                    Карта: %s;
                    Контактные данные: %s;
                    Активен: %s;
                    Валютные подсчета:
                    %s
                        """.formatted(cardEntity.toString(),
                                        contactData,
                                        isActive ? "да" : "нет",
                                        listSubaccounts(currencySubaccounts, "нет"));
    }

    /**
     * An open helper method representing a <String, Double> map as a list of keys and values in form of a string.
     * Can be used outside a concrete account object in other classes dealing with account-like objects.
     * @param accountsMap a Map<String, Double> to be represented.
     * @param noneValue   a string to be substituted when there's no entries in the map.
     * @return  a string containing lines with keys & value or a specified empty value if the map is empty.
     */
    public static String listSubaccounts(Map<String, Integer> accountsMap, String noneValue) {
        StringBuilder accountsList = new StringBuilder();
        if (accountsMap.size() == 0) {
            accountsList.append("\t%s\n".formatted(noneValue));
        } else {
            accountsMap.forEach(
                    (key, value) -> accountsList.append("\t%s: %.2f\n".formatted(key, value / 100.))
            );
        }

        return accountsList.toString();
    }
}
