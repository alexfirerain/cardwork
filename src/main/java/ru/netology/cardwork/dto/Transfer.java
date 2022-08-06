package ru.netology.cardwork.dto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.netology.cardwork.model.CardIdentity;
import ru.netology.cardwork.model.TransferAmount;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.text.ParseException;

/**
 * An object being received as a request on a transfer deal.
 * It harbours a complete card definition of sender,
 * a number of target card and an 'Transfer Amount' object
 * representing the matter to be transferred.
 */
@Getter
@Slf4j
public class Transfer {
    @Valid
    private final CardIdentity cardFrom;
    @NotBlank
    @Pattern(regexp = "\\d{16,}")
    private final String cardTo;
    @Valid
    private final TransferAmount transferAmount;

    public Transfer(String cardFromNumber,
                    String cardFromValidTill,
                    String cardFromCVV,
                    String cardToNumber,
                    TransferAmount amount) throws ParseException {
        this.cardFrom = new CardIdentity(cardFromNumber,
                                cardFromValidTill,
                                cardFromCVV);
        this.cardTo = cardToNumber;
        this.transferAmount = amount;

        log.debug("Transfer constructed: {}", this);
    }

    @Override
    public String toString() {
        return "Перевод с [" + cardFrom + "] на карту №" + cardTo + " {" + transferAmount + "}";
    }
}
