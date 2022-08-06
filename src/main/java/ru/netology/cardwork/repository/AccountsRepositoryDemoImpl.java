package ru.netology.cardwork.repository;

import org.springframework.stereotype.Repository;
import ru.netology.cardwork.dto.Transfer;
import ru.netology.cardwork.exception.CardDataNotValidException;
import ru.netology.cardwork.exception.CardNotFoundException;
import ru.netology.cardwork.model.CardEntity;
import ru.netology.cardwork.model.CardIdentity;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A mock implementation of financial repository capable of transfer operations.
 */
@Repository
public class AccountsRepositoryDemoImpl implements AccountsRepository {

    /**
     * The implementation of banking structure holding a CardEntity object as a key
     * and a map of multiaccaunt as a value. The multiaccaunt holds currency naming
     * as a key and value in Integer as a value. Using integer values is as strange
     * applicable for financials as required in this task.
     */
    private final Map<CardEntity, Map<String, Integer>> cards = new ConcurrentHashMap<>();

    /**
     * Returns all known in this repository card entities as a set.
     * @return a set of all CardEntities in the 'cards' map.
     */
    private Set<CardEntity> cardsData() {
        return cards.keySet();
    }

    /**
     * Puts a CardIdentity object to the repository as a new active CardEntity, assuming contactData
     * to be an empty string. Assigns to it a new empty account in "RUB" currency.
     * @param cardAdding a card to be inserted into the base.
     */
    public void addDefaultEntity(CardIdentity cardAdding) {
        CardEntity newRecord = new CardEntity(cardAdding, "", true);
        Map<String, Integer> cardsFunds = new ConcurrentHashMap<>();
        cardsFunds.put("RUB", 0);
        cards.put(newRecord, cardsFunds);
    }

    /**
     * Retrieves from the repository a CardIdentity object which has given number.
     * @param number a card number in question.
     * @return a CardIdentity object with number in question; {@code null} if such a card is absent.
     */
    private CardIdentity getCardByNumber(String number) {
        for(CardEntity card : cardsData())
            if (card.getCardNumber().equals(number))
                return card.getCardData();

        return null;
    }

    /**
     * Retrieves from the repository a CardEntity object
     * corresponding to the given CardIdentity.
     * @param card card identities in question.
     * @return a fully qualified CardEntity entity, {@code null} if there's no object with such identities in da base.
     */
    private CardEntity getCardByIdentity(CardIdentity card) {
        for(CardEntity cardEntity : cardsData())
            if (cardEntity.getCardData().equals(card))
                return cardEntity;

        return null;
    }

    @Override
    public void commitTransfer(Transfer transferToCommit) {

    }

    @Override
    public boolean containsCardNumber(String number) {
        for(CardEntity card : cards.keySet())
            if (card.getCardNumber().equals(number))
                return true;

        return false;
    }

    @Override
    public boolean isValidCardData(CardIdentity cardIdentity) throws CardNotFoundException {
        String cardRequestedNumber = cardIdentity.getCardNumber();
        CardIdentity cardInQuestion = getCardByNumber(cardRequestedNumber);
        if (cardInQuestion == null)
                throw new CardNotFoundException("Сведений о карте №" + cardRequestedNumber + " нет.");
        return cardInQuestion.equals(cardIdentity);
    }

    /**
     * Reports funds available on given account at given card.
     * @param card     a card on which amount gets requested.
     * @param currency a currency in which amount gets requested.
     * @return state of the account in given currency, {@code 0} if account in given currency is absent at this card.
     * @throws CardNotFoundException    if there's no such a card in the repository.
     * @throws CardDataNotValidException    if any of card data is not the same as in one in the repository.
     */
    @Override
    public int howManyFundsHas(CardIdentity card, String currency) throws CardNotFoundException, CardDataNotValidException {
        if (!isValidCardData(card))
            throw new CardDataNotValidException("Данные карты №" + card.getCardNumber() + " не соответствуют. К сожалению.");
        CardEntity requestedCard = getCardByIdentity(card);
        Map<String, Integer> currencyAccount = cards.get(requestedCard);
        Integer fund = currencyAccount.get(currency);
        return fund == null ? 0 : fund;
    }
}
