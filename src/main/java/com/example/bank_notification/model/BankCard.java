package com.example.bank_notification.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"cardNumber"})
public class BankCard {

    private String id;
    private String cardNumber;
    private String clientId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate issueDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    private String cardType;
    private boolean active;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    public BankCard(String clientId, String cardNumber, LocalDate issueDate,
                    LocalDate expiryDate, String cardType) {
        this.id = UUID.randomUUID().toString();
        this.clientId = clientId;
        this.cardNumber = cardNumber;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.cardType = cardType;
        this.active = true;
        this.createdAt = LocalDate.now();
    }

    public String getMaskedNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String cleanNumber = cardNumber.replaceAll("[^0-9]", "");
        String last4 = cleanNumber.substring(cleanNumber.length() - 4);
        return "**** **** **** " + last4;
    }

    public boolean isExpired(){
        return LocalDate.now().isAfter(expiryDate);
    }

    public long daysUntilExpired(){
        if(isExpired()) return 0;
        return LocalDate.now().until(expiryDate).getDays();
    }
}