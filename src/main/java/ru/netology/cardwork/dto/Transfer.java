package ru.netology.cardwork.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.netology.cardwork.model.Card;
import ru.netology.cardwork.model.TransferAmount;

import javax.validation.Valid;

@AllArgsConstructor
@Getter
public class Transfer {
    @Valid
    private final Card cardFrom;
    private final String cardTo;
    @Valid
    private final TransferAmount transferAmount;

}
