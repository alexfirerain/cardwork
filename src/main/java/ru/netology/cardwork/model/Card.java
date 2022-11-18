package ru.netology.cardwork.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A model object representation of a bank card
 * which serves as a key when storing accounts.
 * Contains a card number (as string), a date this card is valid till
 * (interconvertible with a corresponding string in the form of MM/YY inclusive)
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
     * Last month and year when this card is valid (stored as a date
     * but can be set or presented as a string in a MM/YY pattern).
     *
     */
    @NotNull(message = "должен быть указан срок действия")
    @Future(message = "карта просрочена")
    private Date validTill;
    // TODO: надо чтобы просроченная карта могла быть создана и существовать,
    //  только чтобы не проходила валидацию уже для операций

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
     * @param validTill     the month and year for which the card is valid inclusive.
     *                      If the pattern received not recognized, the zero-date is set.
     * @param cardCVV       an additional CVV of the card.
     */
    public Card(String cardNumber,
                String validTill,
                String cardCVV) {

        this.cardNumber = cardNumber;

        try {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(MONTH_YEAR_FORMATTER.parse(validTill));
            calendar.add(Calendar.MONTH, 1);
            this.validTill = calendar.getTime();
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
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(validTill);
        calendar.add(Calendar.MONTH, -1);
        return MONTH_YEAR_FORMATTER.format(calendar.getTime());
    }

    /**
     * Assures the card to be valid at a pointed moment of time.
     * @param pointInTime a moment of time to be checked.
     * @return {@code true} if the card is valid at the offered moment.
     */
    public boolean isValidAt(Date pointInTime) {
        return validTill.after(pointInTime);
    }
}
