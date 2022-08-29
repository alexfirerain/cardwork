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
     * Using integer values is as queer applicable for financials as required in this task.
     */
    private final Map<String, Integer> currencySubaccounts = new ConcurrentHashMap<>();

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

    public boolean isActive() {
        return isActive;
    }
    public Account setActive() {
        isActive = true;
        return this;
    }
    public Account setInactive() {
        isActive = false;
        return this;
    }
    public Account setContactData(String contactData) {
        this.contactData = contactData;
        return this;
    }

    public boolean noSuchCurrency(String currency) {
        return !currencySubaccounts.containsKey(currency);
    }
    public int fundsOnAccount(String currency) {
        if (noSuchCurrency(currency)) throwIAEofNoCurrency();

        return currencySubaccounts.get(currency);
    }
    public Account addCurrencySubaccount(String currency, int value) {
        currencySubaccounts.put(currency, value);
        return this;
    }

    public Account addCurrencySubaccount(String currency) {
        currencySubaccounts.put(currency, 0);
        return this;
    }

    public double addFunds(String currency, int value) {
        if (noSuchCurrency(currency)) throwIAEofNoCurrency();

        currencySubaccounts.put(currency, currencySubaccounts.get(currency) + value);
        return currencySubaccounts.get(currency);
    }
    public double subtractFunds(String currency, int value) {
        if (noSuchCurrency(currency)) throwIAEofNoCurrency();

        // the card is ABLE of keeping negative accounts, so no checking for negative values needed
        currencySubaccounts.put(currency, currencySubaccounts.get(currency) - value);
        return currencySubaccounts.get(currency);
    }
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
     * Open method representing a <String, Double> map as a list of keys and values in form of a string.
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
