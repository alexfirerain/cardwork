package ru.netology.cardwork.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.netology.cardwork.model.Card;
import ru.netology.cardwork.model.TransferAmount;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * An object being received as a request on a transfer deal.
 * It harbours a complete card definition of sender,
 * a number of target card and an 'Transfer Amount' object
 * representing the matter to be transferred.
 */
@Getter
@Slf4j
@NoArgsConstructor
public class Transfer {
    @Valid
    private Card cardFrom;
    @NotBlank
    @Pattern(regexp = "\\d{16,}", message = "номер карты по меньшей мере 16 цифр")
    private String cardTo;
    @Valid
    private TransferAmount transferAmount;

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

    public Transfer(String cardFromNumber,
                    String cardFromValidTill,
                    String cardFromCVV,
                    String cardToNumber,
                    int amount,
                    String currency) {
        this(cardFromNumber,
                cardFromValidTill,
                cardFromCVV,
                cardToNumber,
                new TransferAmount(amount, currency));
        log.trace("Transfer constructed: {}", this);
    }

    public Transfer(Card cardFrom,
                    Card cardTo,
                    int transferAmount) {
        this.cardFrom = cardFrom;
        this.cardTo = cardTo.getCardNumber();
        this.transferAmount = new TransferAmount(transferAmount, "RUR");
        log.trace("Transfer constructed: {}", this);
    }

    @Override
    public String toString() {
        return "Transfer {%s} #%s → #%s"
                .formatted(transferAmount.toString(), cardFrom.getCardNumber(), cardTo);
    }
}
