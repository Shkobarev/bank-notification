package com.example.bank_notification.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "passport_number", unique = true)
    private String passportNumber;

    @Column(name = "phone")
    private String phone;

    @Column(name = "created_at")
    private LocalDate createdAt;

    public ClientEntity(String fullName, LocalDate birthDate, String email,
                        String passportNumber, String phone) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.email = email;
        this.passportNumber = passportNumber;
        this.phone = phone;
        this.createdAt = LocalDate.now();
    }

    public int getAge() {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
