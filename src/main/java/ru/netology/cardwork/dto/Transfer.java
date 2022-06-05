package ru.netology.cardwork.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.netology.cardwork.model.Card;
import ru.netology.cardwork.model.TransferAmount;

@AllArgsConstructor
@Getter
public class Transfer {
    private final Card cardFrom;
    private final String cardTo;
    private final TransferAmount transferAmount;

}
