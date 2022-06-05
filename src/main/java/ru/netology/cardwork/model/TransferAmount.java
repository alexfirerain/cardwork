package ru.netology.cardwork.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TransferAmount {
    private final Integer value;
    private final String currency;

}
