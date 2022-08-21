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

    void addAccounts(Account[] accounts);

    void resetAccounts();

    void deleteAccount(Card card);

    void reassignAccountToCard(Account oldAccount, Card newCard);
}