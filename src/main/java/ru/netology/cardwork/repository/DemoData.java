package ru.netology.cardwork.repository;

import ru.netology.cardwork.model.Account;
import ru.netology.cardwork.model.Card;


public class DemoData {
    public static final Card CARD_1 = new Card("1234567812345678", "03/25", "125");
    public static final Card CARD_2 = new Card("8765432187654321", "08/25", "555");
    public static final Card CARD_3 = new Card("1122334455667788", "12/27", "128");
    public static final Card CARD_4 = new Card("8877665544332211", "12/24", "646");

    public static final Card CARD_5 = new Card("1111222233334444", "11/25", "259");

    public static final Account ACCOUNT_1 = (new Account(CARD_1))
                              .addCurrencySubaccount("RUR", 300000);
    public static final Account ACCOUNT_2 = (new Account(CARD_2))
                              .addCurrencySubaccount("RUR")
                              .setContactData("post@newage.org");
    public static final Account ACCOUNT_3 = (new Account(CARD_3))
                              .addCurrencySubaccount("RUR", 3000000)
                              .setContactData("на деревню дедушке");
    public static final Account INACTIVE_ACCOUNT = (new Account(CARD_4))
                              .addCurrencySubaccount("RUR", 30000000)
                              .setContactData("+70008885577")
                              .setInactive();
    public static final Account NO_RUR_ACCOUNT = (new Account(CARD_5))
                              .addCurrencySubaccount("USD", 5000000)
                              .setContactData("far street");

    public static final Account[] ALL_ACCOUNTS = {
            ACCOUNT_1,
            ACCOUNT_2,
            ACCOUNT_3,
            INACTIVE_ACCOUNT,
            NO_RUR_ACCOUNT
    };
}
