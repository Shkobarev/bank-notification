package com.example.bank_notification.repository;

import com.example.bank_notification.model.BankCard;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления банковскими картами.
 * Предоставляет методы для хранения, поиска и управления карт.
 */
public interface CardRepository {

    /**
     * Сохраняет или обновляет банковскую карту в репозитории.
     * Если карта с таким ID уже существует, она будет заменена.
     *
     * @param card объект банковской карты для сохранения
     * @return сохраненная банковская карта
     */
    BankCard save(BankCard card);

    /**
     * Находит карту по её уникальному идентификатору.
     *
     * @param id уникальный идентификатор карты
     * @return Optional с найденной картой или empty, если карта не найдена
     */
    Optional<BankCard> findById(String id);

    /**
     * Находит карту по её номеру.
     * Номер карты уникален в системе.
     *
     * @param cardNumber номер карты
     * @return Optional с найденной картой или empty, если карта не найдена
     */
    Optional<BankCard> findByCardNumber(String cardNumber);

    /**
     * Находит все карты, принадлежащие указанному клиенту.
     *
     * @param clientId идентификатор клиента
     * @return список карт клиента. Если клиент не найден, возвращается пустой список
     */
    List<BankCard> findByClientId(String clientId);

    /**
     * Находит все активные карты в системе.
     * Активной считается карта, у которой флаг isActive = true.
     *
     * @return список активных карт
     */
    List<BankCard> findActiveCards();

    /**
     * Находит карты, срок действия которых истекает через указанное количество дней или меньше.
     *
     * @param days количество дней до истечения срока
     * @return список карт с истекающим сроком
     */
    List<BankCard> findExpiringCards(int days);

    /**
     * Возвращает все карты, хранящиеся в репозитории.
     *
     * @return список всех карт
     */
    List<BankCard> findAll();

    /**
     * Удаляет карту по её идентификатору.
     *
     * @param id идентификатор карты для удаления
     * @return true если карта была удалена, false в противном случае
     */
    boolean deleteById(String id);

    /**
     * Удаляет все карты указанного клиента.
     *
     * @param clientId идентификатор клиента
     * @return true если были удалены какие-либо карты, false в противном случае
     */
    boolean deleteByClientId(String clientId);

    /**
     * Проверяет существование карты с указанным номером.
     *
     * @param cardNumber номер карты для проверки
     * @return true если карта с таким номером существует, false в противном случае
     */
    boolean existsByCardNumber(String cardNumber);

    /**
     * Возвращает общее количество карт в репозитории.
     *
     * @return количество карт
     */
    int count();

    /**
     * Возвращает количество активных карт в репозитории.
     *
     * @return количество активных карт
     */
    long countActive();

    /**
     * Возвращает количество карт, срок действия которых истекает через указанное
     * количество дней или меньше.
     *
     * @param days количество дней до истечения срока
     * @return количество карт с истекающим сроком
     */
    long countExpiringCards(int days);
}
