package ru.netology.cardwork.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@Slf4j
@Validated
public class Card {
    @NotBlank(message = "номер карты не должен быть пустым")
    @Pattern(regexp = "\\d{16,}", message = "номер карты по меньшей мере 16 цифр")
    private String cardNumber;

    @NotNull(message = "должен быть указан срок действия")
    @Future(message = "карта просрочена")
    private Date validTill;

    @NotBlank(message = "CVV не должно быть пустым")
    @Size(min = 3, message = "в CVV не меньше трёх символов")
    private String cardCVV;

    /**
     * A model object representation of a bank Card.
     * @param cardNumber    the card's unique id number.
     * @param validTill     month and year in future when the card expires.
     * @param cardCVV       a miscellaneous card's CVV.
     * @throws ParseException   if extraction of date from string not successful.
     */
    public Card(String cardNumber, String validTill, String cardCVV) throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/yy");
        this.cardNumber = cardNumber;
        this.validTill = dateFormatter.parse(validTill);
        this.cardCVV = cardCVV;

        log.debug("Card constructed: {}", this);
    }

    @Override
    public String toString() {
        return "Карта №" + cardNumber + " (действительна по " + validTill + ", CVV " + cardCVV + ")";
    }
}
