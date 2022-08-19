package ru.netology.cardwork.repository;

import ru.netology.cardwork.model.Account;
import ru.netology.cardwork.model.Card;


public class DemoData {
    public static final Card CARD_1 = new Card("1234567812345678", "03/25", "125");
    public static final Card CARD_2 = new Card("8765432187654321", "08/25", "ANB");
    public static final Card CARD_3 = new Card("1122334455667788", "12/27", "A2M");
    public static final Card CARD_4 = new Card("8877665544332211", "12/24", "H4E");

    public static final Account ACCOUNT_1 = (new Account(CARD_1))
                              .addCurrencySubaccount("RUB", 3000);
    public static final Account ACCOUNT_2 = (new Account(CARD_2))
                              .addCurrencySubaccount("RUB")
                              .setContactData("post@newage.org");
    public static final Account ACCOUNT_3 = (new Account(CARD_3))
                              .addCurrencySubaccount("RUB", 30000)
                              .setContactData("на деревню дедушке");
    public static final Account ACCOUNT_4 = (new Account(CARD_4))
                              .addCurrencySubaccount("RUB", 300000)
                              .setContactData("+70008885577")
                              .setInactive();

    public static final Account[] ALL_ACCOUNTS = {
            ACCOUNT_1,
            ACCOUNT_2,
            ACCOUNT_3,
            ACCOUNT_4
    };
}
