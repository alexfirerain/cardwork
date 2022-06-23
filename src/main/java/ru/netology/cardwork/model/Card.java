package ru.netology.cardwork.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class Card {
    @NotBlank
    @Pattern(regexp = "\\d{16,}")
    private String cardNumber;

    @NotBlank
    @DateTimeFormat(pattern = "MM/YY")  // TODO: supply with concrete date check
    @Future
    @EqualsAndHashCode.Exclude
    private String ValidTill;

    @NotBlank
    @Size(min = 3)
    @EqualsAndHashCode.Exclude
    private String cardCVV;

}
