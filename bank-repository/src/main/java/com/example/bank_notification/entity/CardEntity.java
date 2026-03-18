package com.example.bank_notification.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
public class CardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "card_number", unique = true, nullable = false)
    private String cardNumber;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "card_type", nullable = false)
    private String cardType;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "created_at")
    private LocalDate createdAt;

    public CardEntity(ClientEntity client, String cardNumber, LocalDate issueDate,
                      LocalDate expiryDate, String cardType) {
        this.client = client;
        this.cardNumber = cardNumber;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.cardType = cardType;
        this.active = true;
        this.createdAt = LocalDate.now();
    }

    public String getMaskedNumber() {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        String cleanNumber = cardNumber.replaceAll("[^0-9]", "");
        String last4 = cleanNumber.substring(cleanNumber.length() - 4);
        return "****-****-****-" + last4;
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    public long daysUntilExpired() {
        if (isExpired()) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }
}
