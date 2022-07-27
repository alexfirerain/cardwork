package ru.netology.cardwork.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@Validated
public class Card {
    @NotBlank
    @Pattern(regexp = "\\d{16,}")
    private String cardNumber;

    @NotBlank
    @DateTimeFormat(pattern = "MM/yy")  // TODO: supply with concrete date check
    @Future
    @EqualsAndHashCode.Exclude
    private Date ValidTill;

    @NotBlank
    @Size(min = 3)
    @EqualsAndHashCode.Exclude
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
        ValidTill = dateFormatter.parse(validTill);
        this.cardCVV = cardCVV;

        System.out.println("Card constructed: " + this);    // monitor
    }

    @Override
    public String toString() {
        return "Карта №" + cardNumber + " (действительна по " + ValidTill + ", CVV " + cardCVV + ")";
    }
}
