package ru.netology.cardwork.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class CardEntity {
    private CardIdentity cardData;
    private String contactData;
    private boolean isActive;

    public String getCardNumber() {
        return cardData.getCardNumber();
    }
    public Date getCardValidTill() {
        return cardData.getValidTill();
    }
    public String getCardCVV() {
        return cardData.getCardCVV();
    }
}
