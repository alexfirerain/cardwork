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
        log.debug(this.toString());
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
        return currencySubaccounts.containsKey(currency);
    }
    public int fundsOnAccount(String currency) {
        if (!hasCurrencyAccount(currency)) throwIAEofNoCurrency();

        return currencySubaccounts.get(currency);
    }
    public Account addCurrencySubaccount(String currency, int value) {
        currencySubaccounts.put(currency, value);
        return this;
    }

    public int addFunds(String currency, int value) {
        if (!hasCurrencyAccount(currency)) throwIAEofNoCurrency();

        currencySubaccounts.put(currency, currencySubaccounts.get(currency) + value);
        return currencySubaccounts.get(currency);
    }
    public int subtractFunds(String currency, int value) {
        if (!hasCurrencyAccount(currency)) throwIAEofNoCurrency();

        // the card is ABLE of keeping negative accounts, so no checking for negative values needed
        currencySubaccounts.put(currency, currencySubaccounts.get(currency) - value);
        return currencySubaccounts.get(currency);
    }
    private void throwIAEofNoCurrency() {
        throw new IllegalArgumentException("no such currency @card#" + getCardNumber());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("""
                    Счёт:
                        Карта: %s;
                        Контактные данные: %s;
                        Активен: %s;
                        Валютные подсчета:
                            """.formatted(cardEntity.toString(), contactData, isActive ? "да" : "нет"));

        if (currencySubaccounts.size() == 0)
            sb.append("нет%n");
        else
            currencySubaccounts.forEach(
                (key, value) -> sb.append("%s: %d%n".formatted(key, value))
            );
        return sb.toString();
    }
}
