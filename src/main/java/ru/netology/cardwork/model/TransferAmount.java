package ru.netology.cardwork.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Representation for the particular transfer's properties: amount & currency.
 */
@Getter
@Slf4j
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

    public TransferAmount(@NotNull @Positive Integer value, @NotBlank String currency) {
        this.value = value;
        this.currency = currency;

        log.debug("TransferAmount constructed: {}", this);
    }

    @Override
    public String toString() {
        return value.toString() + currency;
    }
}
