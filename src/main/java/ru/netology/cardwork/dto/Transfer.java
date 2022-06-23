package ru.netology.cardwork.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
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
@AllArgsConstructor
@Getter
public class Transfer {
    @Valid
    private final Card cardFrom;
    @NotBlank
    @Pattern(regexp = "\\d{16,}")
    private final String cardTo;
    @Valid
    private final TransferAmount transferAmount;

}
