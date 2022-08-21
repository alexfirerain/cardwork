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
    private final Map<String, Double> currencySubaccounts = new ConcurrentHashMap<>();

    public Account(Card cardAdding) {
        this(cardAdding,  "", true);
        log.debug("Account constructed: {}", this);
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
    public double fundsOnAccount(String currency) {
        if (noSuchCurrency(currency)) throwIAEofNoCurrency();

        return currencySubaccounts.get(currency);
    }
    public Account addCurrencySubaccount(String currency, double value) {
        currencySubaccounts.put(currency, value);
        return this;
    }

    public Account addCurrencySubaccount(String currency) {
        currencySubaccounts.put(currency, 0.);
        return this;
    }

    public double addFunds(String currency, double value) {
        if (noSuchCurrency(currency)) throwIAEofNoCurrency();

        currencySubaccounts.put(currency, currencySubaccounts.get(currency) + value);
        return currencySubaccounts.get(currency);
    }
    public double subtractFunds(String currency, double value) {
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

    public static String listSubaccounts(Map<String, Double> accountsMap, String noneValue) {
        StringBuilder accountsList = new StringBuilder();
        if (accountsMap.size() == 0) {
            accountsList.append("\t%s\n".formatted(noneValue));
        } else {
            accountsMap.forEach(
                    (key, value) -> accountsList.append("\t%s: %.2f\n".formatted(key, value))
            );
        }

        return accountsList.toString();
    }
}
