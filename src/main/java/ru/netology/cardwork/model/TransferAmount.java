package ru.netology.cardwork.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Representation for the particular transfer's properties: amount & currency.
 */
@AllArgsConstructor
@Getter
@Validated
public class TransferAmount {
    /**
     * Amount of funds to transfer.
     */
    @NotNull
    @Positive
    private final Integer value;
    /**
     * Currency of funds to transfer.
     */
    @NotBlank
    private final String currency;

}
