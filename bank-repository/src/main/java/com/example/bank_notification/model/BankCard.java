package com.example.bank_notification.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Модель банковской карты клиента.
 * Содержит информацию о платежной карте, ее статусе, сроках действия и принадлежности клиенту.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"cardNumber"})
public class BankCard {

    /**
     * Уникальный идентификатор карты в системе.
     * Генерируется автоматически при создании новой карты.
     * Формат: UUID (36 символов).
     */
    private String id;

    /**
     * Номер банковской карты.
     * Уникальное значение в системе. Используется для идентификации карты.
     * Формат: XXXX-XXXX-XXXX-XXXX (16 цифр с дефисами)
     * Пример: "1234-5678-9012-3456"
     */
    private String cardNumber;

    /**
     * Идентификатор клиента-владельца карты.
     * Один клиент может иметь несколько карт.
     */
    private String clientId;

    /**
     * Дата выпуска карты.
     * Устанавливается при создании карты.
     * Формат: yyyy-MM-dd
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate issueDate;

    /**
     * Дата истечения срока действия карты.
     * Формат: yyyy-MM-dd
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    /**
     * Тип платежной системы карты.
     * Допустимые значения: "VISA", "Mastercard", "MIR"
     */
    private String cardType;

    /**
     * Статус активности карты.
     * <ul>
     *   <li>{@code true} - карта активна и может использоваться</li>
     *   <li>{@code false} - карта аннулирована (заблокирована или закрыта)</li>
     * </ul>
     */
    private boolean active;

    /**
     * Дата и время создания записи о карте.
     * Устанавливается автоматически при создании объекта.
     * Формат: yyyy-MM-dd
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    /**
     * Конструктор для создания новой банковской карты.
     * Генерирует уникальный ID и устанавливает статус {@code active = true}.
     *
     * @param clientId идентификатор клиента-владельца
     * @param cardNumber номер карты
     * @param issueDate дата выпуска
     * @param expiryDate дата истечения срока
     * @param cardType тип карты (VISA, Mastercard, MIR)
     */
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

    /**
     * Проверяет, истек ли срок действия карты.
     *
     * @return {@code true} если {@link #expiryDate} раньше текущей даты,
     *         {@code false} в противном случае
     */
    public boolean isExpired(){
        return LocalDate.now().isAfter(expiryDate);
    }

    /**
     * Вычисляет количество дней, оставшихся до истечения срока карты.
     *
     * @return количество дней до {@link #expiryDate},
     *         или 0 если {@link #isExpired()} возвращает {@code true}
     */
    public long daysUntilExpired(){
        if(isExpired()) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(),expiryDate);
    }
}