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

    public Card(String cardNumber, String validTill, String cardCVV) throws ParseException {
        System.out.println("Card constructor: " + cardNumber + validTill + cardCVV);         // monitor
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/yy");
        this.cardNumber = cardNumber;
        ValidTill = dateFormatter.parse(validTill);
        this.cardCVV = cardCVV;

        System.out.println("Card constructed: " + this);
    }
}
