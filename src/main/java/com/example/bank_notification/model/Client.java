package com.example.bank_notification.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

/**
 * Модель клиента банка.
 * Содержит персональные данные клиента и информацию о его регистрации в системе.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"fullName", "birthDate"})
public class Client {

    /**
     * Уникальный идентификатор клиента в системе.
     * Генерируется автоматически при создании нового клиента.
     * Формат: UUID (36 символов).
     */
    private String id;

    /**
     * Полное имя клиента (ФИО).
     * Обязательное поле.
     */
    private String fullName;

    /**
     * Дата рождения клиента.
     * Обязательное поле.
     * Формат: yyyy-MM-dd
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    /**
     * Возраст клиента.
     * Вычисляется автоматически на основе {@link #birthDate}.
     * Не хранится в базе данных, а рассчитывается при каждом запросе.
     */
    private int age;

    /**
     * Адрес электронной почты клиента.
     * Должен быть уникальным.
     * Формат: example@domain.com
     */
    private String email;

    /**
     * Номер паспорта клиента.
     * Опциональное поле.
     * Если указан, должен быть уникальным.
     * Формат: "1234 567890"
     */
    private String passportNumber;

    /**
     * Номер телефона клиента.
     * Опциональное поле.
     * Формат: 89001234567
     */
    private String phone;

    /**
     * Дата и время создания записи о клиенте.
     * Устанавливается автоматически при создании объекта.
     * Не изменяется после создания.
     * Формат: yyyy-MM-dd
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    /**
     * Конструктор для создания нового клиента.
     * Генерирует уникальный ID и устанавливает текущую дату как дату создания.
     *
     * @param fullName полное имя клиента
     * @param birthDate дата рождения
     * @param email электронная почта
     * @param passportNumber номер паспорта
     * @param phone номер телефона
     */
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

    /**
     * Устанавливает новую дату рождения и автоматически пересчитывает возраст.
     *
     * @param birthDate новая дата рождения
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        this.age = calculateAge();
    }

    /**
     * Вычисляет возраст клиента на основе даты рождения.
     *
     * @return возраст клиента в годах, или 0 если {@link #birthDate} не указана
     */
    private int calculateAge() {
        if (birthDate == null) {
            return 0;
        } else {
            return Period.between(birthDate, LocalDate.now()).getYears();
        }
    }
}

