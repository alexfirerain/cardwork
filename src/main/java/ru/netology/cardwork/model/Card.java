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

/**
 * A model object representation of a bank card
 * which serves as a key when storing accounts.
 * Contains a card number (as string), a date this card is valid till
 * (interconvertible with a corresponding string of the MM/YY form)
 * and a so-called card CVV (one more string).
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@Slf4j
@Validated
public class Card {

    /**
     * A string corresponding to the card's number.
     */
    @NotBlank(message = "номер карты не должен быть пустым")
    @Pattern(regexp = "\\d{16,}", message = "номер карты по меньшей мере 16 цифр")
    private String cardNumber;

    /**
     * A month and a year this card is valid till (stored as a date
     * but can be set or presented as a string in a MM/YY pattern).
     * In current implementation, the stored month means the first month
     * when the card is NOT valid anymore. If you want the month
     * to be understood as inclusive, the implementation is to be changed.
     */
    @NotNull(message = "должен быть указан срок действия")
    @Future(message = "карта просрочена")
    private Date validTill;

    /**
     * A special 'Card Verification Value', an additional string to identify the card.
     * This is a number encrypted in the magnetic strip of cards
     * of the Visa international payment system and here in model.
     */
    @NotBlank(message = "CVV не бывает пустым")
    @Size(min = 3, message = "в CVV не меньше трёх символов")
    private String cardCVV;

    /**
     * An internal transformer of date to be represented either a Date or a String object.
     */
    static private final SimpleDateFormat MONTH_YEAR_FORMATTER = new SimpleDateFormat("MM/yy");

    /**
     * A definition of a bank Card via three strings.
     * @param cardNumber    the card's unique id number.
     * @param validTill     month and year in future when the card expires.
     *                      If the pattern received not recognized, the zero-date is set.
     * @param cardCVV       an additional CVV of the card.
     */
    public Card(String cardNumber,
                String validTill,
                String cardCVV) {

        this.cardNumber = cardNumber;
        try {
            this.validTill = MONTH_YEAR_FORMATTER.parse(validTill);
        } catch (ParseException e) {
            log.warn("the date pattern not recognized, a zero date is set to card#{}", cardNumber);
            this.validTill = new Date(0L);
        }
        this.cardCVV = cardCVV;

        log.trace("Card constructed: {}", this);
    }

    @Override
    public String toString() {
        return "Card #%s (valid till %s, CVV %s)"
                .formatted(cardNumber, getValidTillString(), cardCVV);
    }

    /**
     * A getter to present the expiration date as a 'MM/YY' string.
     * @return a string representation of month and year of card's being valid.
     */
    public String getValidTillString() {
        return MONTH_YEAR_FORMATTER.format(validTill);
    }
}
