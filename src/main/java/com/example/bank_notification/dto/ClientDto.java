package com.example.bank_notification.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Data
public class ClientDto {
    private String id;
    private String fullName;
    private LocalDate birthDate;
    private int age;
    private String email;
    private String phone;
    private String passportNumber;
    private List<String> cardIds;
    private LocalDate createdAt;
}
