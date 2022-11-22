package ru.netology.cardwork.repository;

import ru.netology.cardwork.model.Account;
import ru.netology.cardwork.model.Card;
import ru.netology.cardwork.model.Transfer;

/**
 * A container for demo data of predefined cards, accounts and transfers.
 */
public class DemoData {

    ////////////////
    //   CARDS    //
    ////////////////
    public static final Card CARD_1 = new Card("1234567812345678", "03/25", "125");
    public static final Card CARD_2 = new Card("8765432187654321", "08/25", "555");
    public static final Card CARD_3 = new Card("1122334455667788", "12/27", "128");
    public static final Card CARD_4 = new Card("8877665544332211", "12/24", "646");

    public static final Card CARD_5 = new Card("1111222233334444", "11/25", "259");

    /////////////////
    //   ACCOUNTS  //
    /////////////////

    public static final Account ACCOUNT_1 = (new Account(CARD_1))
                              .addCurrencySubaccount("RUR", 3_000_00);
    public static final Account ACCOUNT_2 = (new Account(CARD_2))
                              .addCurrencySubaccount("RUR")
                              .setContactData("post@newage.org");
    public static final Account ACCOUNT_3 = (new Account(CARD_3))
                              .addCurrencySubaccount("RUR", 30_000_00)
                              .setContactData("на деревню дедушке");
    public static final Account INACTIVE_ACCOUNT = (new Account(CARD_4))
                              .addCurrencySubaccount("RUR", 300_000_00)
                              .setContactData("+70008885577")
                              .setInactive();
    public static final Account NO_RUR_ACCOUNT = (new Account(CARD_5))
                              .addCurrencySubaccount("USD", 50_000_00)
                              .setContactData("far street");

    public static final Account[] ALL_ACCOUNTS = {
            ACCOUNT_1,
            ACCOUNT_2,
            ACCOUNT_3,
            INACTIVE_ACCOUNT,
            NO_RUR_ACCOUNT
    };

    ////////////////////
    //   TRANSFERS    //
    ////////////////////
    /**
     * A transfer of 50₽ from account 1 (3.000₽) to account 2 (0₽)
     */
    public static final Transfer TRANSFER_1 = Transfer.forDemoData(CARD_1, CARD_2, 5000);
}
