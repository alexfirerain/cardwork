package ru.netology.cardwork.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@Getter
@Validated
public class TransferAmount {
    @NotNull
    @Positive
    private final Integer value;
    @NotBlank
    private final String currency;

}
