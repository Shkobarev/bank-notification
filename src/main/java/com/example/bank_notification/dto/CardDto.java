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
}
