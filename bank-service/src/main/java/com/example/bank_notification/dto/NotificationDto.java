package com.example.bank_notification.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class NotificationDto {
    private String clientId;
    private String clientName;
    private String clientEmail;
    private String cardId;
    private String maskedCardNumber;
    private LocalDate expiryDate;
    private long daysUntilExpired;
    private String notificationType;
    private LocalDateTime sentAt;
    private boolean success;
    private String errorMessage;
}
