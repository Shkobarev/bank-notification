package com.example.bank_notification.service;

import com.example.bank_notification.dto.CardDto;

import java.util.List;
import java.util.Optional;


/**
 * Сервис для управления банковскими картами клиентов.
 */
public interface CardService {

    /**
     * Создает новую банковскую карту для клиента.
     *
     * @param clientId идентификатор клиента-владельца
     * @param cardType тип карты (VISA, Mastercard, MIR)
     * @param validityYears срок действия в годах (от 1 до 5), по умолчанию 3
     * @return созданная карта в виде {@link CardDto}
     */
    CardDto createCard(String clientId, String cardType, Integer validityYears);

    /**
     * Возвращает все карты клиента.
     * <p>
     * Возвращает как активные, так и неактивные карты клиента.
     * Список может быть пустым, если у клиента нет карт.
     * </p>
     *
     * @param clientId идентификатор клиента
     * @return список карт клиента в виде {@link CardDto}
     */
    List<CardDto> getClientCards(String clientId);

    /**
     * Возвращает карту по ее идентификатору.
     *
     * @param cardId идентификатор карты
     * @return {@link Optional}, содержащий карту в виде {@link CardDto}, если найдена, иначе {@link Optional#empty()}
     */
    Optional<CardDto> getCard(String cardId);

    /**
     * Аннулирует (блокирует) карту.
     *
     * @param cardId идентификатор карты
     * @return {@code true} если карта успешно аннулирована, {@code false} если карта не найдена или уже неактивна
     */
    boolean cancelCard(String cardId);

    /**
     * Возвращает все активные карты, срок действия которых истекает
     * ровно через указанное количество дней.
     * <p>
     * Просроченные карты (expired) не включаются в результат.
     * </p>
     *
     * @param days количество дней до истечения срока (должно быть >= 0)
     * @return список истекающих карт в виде {@link CardDto} (может быть пустым)
     */
    List<CardDto> getCardsExpiringExactly(int days);

    /**
     * Возвращает все активные карты, срок действия которых истекает
     * через указанное количество дней или меньше.
     * <p>
     * Просроченные карты (expired) не включаются в результат.
     * </p>
     *
     * @param days количество дней до истечения срока (должно быть >= 0)
     * @return список истекающих карт в виде {@link CardDto} (может быть пустым)
     */
    List<CardDto> getExpiringCards(int days);

    /**
     * Возвращает только активные карты клиента.
     *
     * @param clientId идентификатор клиента
     * @return список активных карт клиента в виде {@link CardDto} (может быть пустым)
     */
    List<CardDto> getActiveClientCards(String clientId);

    /**
     * Проверяет существование карты с указанным идентификатором.
     *
     * @param cardId идентификатор карты
     * @return {@code true} если карта существует, иначе {@code false}
     */
    boolean cardExists(String cardId);

    /**
     * Проверяет уникальность номера карты.
     *
     * @param cardNumber номер карты в формате XXXX-XXXX-XXXX-XXXX
     * @return {@code true} если номер уникален, иначе {@code false}
     */
    boolean isCardNumberUnique(String cardNumber);
}
