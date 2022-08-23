package ru.netology.cardwork.repository;

import ru.netology.cardwork.model.Account;
import ru.netology.cardwork.model.Card;

/**
 * Contains abstract functionality for a repository fitted for storing and
 * overall management of money accounts (suitable as in the MoneyTransferServiceSpecification).
 * With this, accounts can be added, deleted, purged or changed.
 */
public interface ManageableAccountRepository {
    /**
     * Puts a Card object to the repository as a new active Account, assuming contactData
     * to be an empty string. Assigns to it a new empty account in "RUB" currency.
     *
     * @param cardAdding a card to be inserted into the base.
     */
    void addDefaultAccount(Card cardAdding);

    /**
     * Just adds a new ready account to the repository.
     *
     * @param account which is added.
     */
    void addAccount(Account account);

    /**
     * Adds an array of accounts to the repository.
     * @param accounts  an array of accounts to add.
     */
    void addAccounts(Account[] accounts);

    /**
     * Purges all accounts from the repository.
     */
    void resetAccounts();

    /**
     * Removes an account associated with given card.
     * @param card a card to delete the account associated with.
     */
    void deleteAccount(Card card);

    /**
     * Bounds the account to a new card.
     * @param oldAccount an account to be reassigned.
     * @param newCard    a new card the account is to be bound to.
     */
    void reassignAccountToCard(Account oldAccount, Card newCard);

    /**
     * Returns a string reporting the state of all accounts in the repository.
     * @return a string representation of funds in currencies stored in the accounts in the repository.
     */
    String reportAccountsState();
}
