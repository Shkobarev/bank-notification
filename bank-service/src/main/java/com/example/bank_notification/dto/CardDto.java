package com.example.bank_notification.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CardDto {
    private String id;
    private String cardNumber;
    private String cardholderName;
    private String clientId;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String cardType;
    private boolean active;
    private boolean expired;
    private long daysUntilExpired;
    private LocalDate createdAt;

    /**
     * Возвращает маскированный номер карты.
     * Отображаются только последние 4 цифры, остальные заменены звездочками.
     * Пример: "****-****-****-3456"
     *
     * @return маскированный номер карты
     *         возвращает "****", если номер карты отсутствует
     */
    public String getMaskedNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String cleanNumber = cardNumber.replaceAll("[^0-9]", "");
        String last4 = cleanNumber.substring(cleanNumber.length() - 4);
        return "****-****-****-" + last4;
    }
}
