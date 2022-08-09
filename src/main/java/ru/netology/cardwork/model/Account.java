package ru.netology.cardwork.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@AllArgsConstructor
public class Account {
    private Card cardEntity;
    private String contactData;
    private boolean isActive;
    /**
     * The multiaccaunt holding currency naming as a key
     * and value in Integer as a value.
     * Using integer values is as strange applicable for financials as required in this task.
     */
    private Map<String, Integer> currencyAccounts;

    public Account(Card cardAdding) {
        this(cardAdding,  "", true, new ConcurrentHashMap<>());
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
    public void setActive() {
        isActive = true;
    }
    public void setInactive() {
        isActive = false;
    }
    public boolean hasCurrencyAccount(String currency) {
        return currencyAccounts.containsKey(currency);
    }
    public int fundsOnAccount(String currency) {
        if (!hasCurrencyAccount(currency)) throwIAEofNoCurrency();

        return currencyAccounts.get(currency);
    }
    public Account addAccount(String currency, int value) {
        currencyAccounts.put(currency, value);
        return this;
    }

    public int addFunds(String currency, int value) {
        if (!hasCurrencyAccount(currency)) throwIAEofNoCurrency();

        currencyAccounts.put(currency, currencyAccounts.get(currency) + value);
        return currencyAccounts.get(currency);
    }
    public int subtractFunds(String currency, int value) {
        if (!hasCurrencyAccount(currency)) throwIAEofNoCurrency();

        // the card is ABLE of keeping negative accounts, so no checking for negative values needed
        currencyAccounts.put(currency, currencyAccounts.get(currency) - value);
        return currencyAccounts.get(currency);
    }
    private void throwIAEofNoCurrency() {
        throw new IllegalArgumentException("no such currency @card#" + getCardNumber());
    }
}
