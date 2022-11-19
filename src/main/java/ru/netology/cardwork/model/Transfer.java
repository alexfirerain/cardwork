package ru.netology.cardwork.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * An object being received as a request on a transfer deal.
 * It harbours a complete card definition of sender,
 * a target card's number and an 'Transfer Amount' object
 * representing the matter to be transacted.
 */
@EqualsAndHashCode
@Getter
@Slf4j
public class Transfer {
    /**
     * A model of the sender's card.
     */
    @Valid
    private final Card cardFrom;
    /**
     *  A number of the card to transfer funds to.
     */
    @NotBlank
    @Pattern(regexp = "\\d{16,}", message = "номер карты по меньшей мере 16 цифр")
    private final String cardTo;
    /**
     * A presentation of the matter to be transferred consisting of amount and currency.
     */
    @Valid
    private final TransferAmount transferAmount;

    /**
     * A default constructor for a Transfer corresponding to a json request
     * according to the specification MoneyTransferServiceSpecification.
     * @param cardFromNumber    a number of sender card.
     * @param cardFromValidTill    an expiration date of sender card as MM/YY.
     * @param cardFromCVV   a CVV of sender card.
     * @param cardToNumber         a number of target card.
     * @param amount    a TransferAmount object to be transacted, consisting of an integer value and a currency code.
     */
    public Transfer(String cardFromNumber,
                    String cardFromValidTill,
                    String cardFromCVV,
                    String cardToNumber,
                    TransferAmount amount) {
        this.cardFrom = new Card(cardFromNumber,
                                cardFromValidTill,
                                cardFromCVV);
        this.cardTo = cardToNumber;
        this.transferAmount = amount;

        log.trace("Transfer constructed: {}", this);
    }

    /**
     * A creator for a new transfer via simple arguments
     * @param cardFromNumber    a number of sender card.
     * @param cardFromValidTill    an expiration date of sender card as MM/YY.
     * @param cardFromCVV   a CVV of sender card.
     * @param cardToNumber         a number of target card.
     * @param amount    an amount to be transferred.
     * @param currency   a currency in which to perform the transfer.
     * @return  a new defined Transfer object.
     */
    public static Transfer fromSeparateArguments(String cardFromNumber,
                                                String cardFromValidTill,
                                                String cardFromCVV,
                                                String cardToNumber,
                                                int amount,
                                                String currency) {
        return new Transfer(cardFromNumber,
                            cardFromValidTill,
                            cardFromCVV,
                            cardToNumber,
                            new TransferAmount(amount, currency));
    }

    /**
     * A practical creator for a new Transfer based on two known Cards
     * and an amount to transfer in kopecks (having a "RUR" currency as default).
     * @param cardFrom       a card the transfer is performed from.
     * @param cardTo    a card the transfer is performed to.
     * @param transferAmount    an amount in kopecks to be transferred.
     * @return  a new defined Transfer object.
     */
    public static Transfer forDemoData(Card cardFrom,
                                        Card cardTo,
                                        int transferAmount) {
        return new Transfer(cardFrom.getCardNumber(),
                            cardFrom.getValidTillString(),
                            cardFrom.getCardCVV(),
                            cardTo.getCardNumber(),
                            new TransferAmount(transferAmount, "RUR"));
    }



    @Override
    public String toString() {
        return "Transfer {%s} #%s → #%s"
                .formatted(transferAmount.toString(), cardFrom.getCardNumber(), cardTo);
    }

    public String toJson() {
        return ("{ \"cardFromNumber\": \"%s\"," +
                " \"cardFromValidTill\": \"%s\"," +
                " \"cardFromCVV\": \"%s\"," +
                " \"cardToNumber\": \"%s\"," +
                " \"amount\": { \"value\": %d," +
                " \"currency\": \"%s\" } }")
                .formatted(cardFrom.getCardNumber(),
                        cardFrom.getValidTillString(),
                        cardFrom.getCardCVV(),
                        cardTo,
                        transferAmount.getValue(),
                        transferAmount.getCurrency());
    }
}
