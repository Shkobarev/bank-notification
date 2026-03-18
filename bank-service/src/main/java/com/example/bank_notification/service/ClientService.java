package com.example.bank_notification.service;

import com.example.bank_notification.dto.ClientDto;
import com.example.bank_notification.dto.ClientCreationResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления клиентами банка.
 */
public interface ClientService {

    /**
     * Создает нового клиента или возвращает существующего.
     *
     * @param fullName полное имя клиента (ФИО)
     * @param birthDate дата рождения
     * @param email электронная почта
     * @param phone номер телефона
     * @param passportNumber номер паспорта в формате "1234 567890"
     * @return созданный или существующий клиент в виде {@link ClientDto}
     */
    ClientDto createClient(String fullName, LocalDate birthDate,
                           String email, String phone, String passportNumber);

    /**
     * Создает нового клиента или возвращает существующего с флагом создания.
     *
     * @param fullName полное имя клиента (ФИО)
     * @param birthDate дата рождения
     * @param email электронная почта
     * @param phone номер телефона
     * @param passportNumber номер паспорта в формате "1234 567890"
     * @return созданный или существующий клиент в виде {@link ClientCreationResult}
     */
    ClientCreationResult createClientWithResult(String fullName, LocalDate birthDate,
                                                String email, String phone, String passportNumber);

    /**
     * Возвращает клиента по его уникальному идентификатору.
     * <p>
     * Метод используется для получения полной информации о клиенте,
     * включая список идентификаторов его карт.
     * </p>
     *
     * @param id уникальный идентификатор клиента
     * @return {@link Optional}, содержащий клиента в виде {@link ClientDto}, если найден,
     * иначе {@link Optional#empty()}
     */
    Optional<ClientDto> getClient(String id);

    /**
     * Возвращает список всех клиентов.
     * <p>
     * Возвращает всех клиентов, зарегистрированных в системе.
     * Для каждого клиента также возвращаются идентификаторы его карт.
     * </p>
     *
     * @return список всех клиентов в виде {@link ClientDto}
     */
    List<ClientDto> getAllClients();

    /**
     * Обновляет email клиента.
     *
     * @param clientId уникальный идентификатор клиента
     * @param newEmail новый email
     * @return {@link Optional}, содержащий обновленный клиент в виде {@link ClientDto}
     */
    Optional<ClientDto> updateEmail(String clientId, String newEmail);

    /**
     * Удаляет клиента по ID.
     * <p>
     * Удаление возможно только если у клиента нет активных карт.
     * Перед удалением проверяется наличие активных карт через {@link CardService}.
     * </p>
     *
     * @param id уникальный идентификатор клиента
     * @return {@code true} если клиент успешно удален, иначе {@code false}
     */
    boolean deleteClient(String id);

    /**
     * Проверяет существование клиента с указанным ID.
     *
     * @param id уникальный идентификатор клиента
     * @return {@code true} если клиент существует, иначе {@code false}
     */
    boolean clientExists(String id);

    /**
     * Ищет клиента по email.
     * <p>
     * Поиск выполняется по точному совпадению email.
     * Email должен быть уникальным в системе.
     * </p>
     *
     * @param email email для поиска
     * @return {@link Optional}, содержащий клиента в виде {@link ClientDto}, если найден, иначе {@link Optional#empty()}
     */
    Optional<ClientDto> findClientByEmail(String email);

    /**
     * Ищет клиента по полному имени и дате рождения.
     *
     * @param fullName полное имя клиента (ФИО)
     * @param birthDate дата рождения
     * @return {@link Optional}, содержащий клиента в виде {@link ClientDto}, если найден, иначе {@link Optional#empty()}
     */
    Optional<ClientDto> findClientByFullNameAndBirthDate(String fullName, LocalDate birthDate);
}