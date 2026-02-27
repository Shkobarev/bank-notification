package com.example.bank_notification.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"fullName", "birthDate"})
public class Client {

    private String id;
    private String fullName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private int age;
    private String email;
    private String passportNumber;
    private String phone;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    public Client(String fullName,LocalDate birthDate,
                  String email, String passportNumber,
                  String phone){
        this.id = UUID.randomUUID().toString();
        this.fullName = fullName;
        this. birthDate = birthDate;
        this.age = calculateAge();
        this.email = email;
        this.passportNumber = passportNumber;
        this.phone = phone;
        this.createdAt = LocalDate.now();

    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        this.age = calculateAge();
    }

    private int calculateAge() {
        if (birthDate == null) {
            return 0;
        } else {
            return Period.between(LocalDate.now(),birthDate).getYears();
        }
    }
}

