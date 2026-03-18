package com.example.bank_notification.repository;

import com.example.bank_notification.model.Client;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с клиентами банка.
 * Предоставляет методы для хранения, поиска и управления клиентами.
 */
 public interface ClientRepository {

   /**
    * Сохраняет клиента в хранилище.
    * Если клиент с таким ID уже существует, он будет перезаписан.
    *
    * @param client объект клиента для сохранения
    * @return сохраненный клиент
    */
    Client save(Client client);

   /**
    * Ищет клиента по уникальному идентификатору.
    *
    * @param id идентификатор клиента
    * @return Optional, содержащий клиента если найден, иначе пустой Optional
    */
    Optional<Client> findById(String id);

   /**
    * Ищет клиента по полному имени и дате рождения.
    *
    * @param fullName полное имя клиента
    * @param birthDate дата рождения клиента
    * @return Optional, содержащий клиента если найден, иначе пустой Optional
    */
    Optional<Client> findByFullNameAndBirthDate(String fullName, LocalDate birthDate);

    /**
     * Ищет клиента по адресу электронной почты.
     *
     * @param email адрес электронной почты
     * @return Optional, содержащий клиента если найден, иначе пустой Optional
     */
    Optional<Client> findByEmail(String email);

    /**
     * Обновляет email клиента.
     * Автоматически обновляет индекс email для поддержания целостности данных.
     *
     * @param clientId идентификатор клиента
     * @param newEmail новый адрес электронной почты
     * @return Optional, содержащий обновленного клиента или пустой Optional если клиент не найден
     */
    Optional<Client> updateEmail(String clientId, String newEmail);

    /**
     * Возвращает список всех клиентов в хранилище.
     *
     * @return список всех клиентов (может быть пустым)
     */
    List<Client> findAll();

    /**
     * Удаляет клиента по идентификатору.
     *
     * @param id идентификатор клиента
     * @return true если клиент был удален, false если клиент не найден
     */
    boolean deleteById(String id);

    /**
     * Возвращает общее количество клиентов в хранилище.
     *
     * @return количество клиентов
     */
    long count();

    /**
     * Проверяет существование клиента по идентификатору.
     *
     * @param id идентификатор клиента
     * @return true если клиент существует, false в противном случае
     */
    boolean existsById(String id);
}
