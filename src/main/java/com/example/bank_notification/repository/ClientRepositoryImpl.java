package com.example.bank_notification.repository;

import com.example.bank_notification.model.Client;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реализация репозитория для работы с клиентами.
 * Хранит данные в оперативной памяти с использованием индексов для быстрого поиска.
 */
@Repository
public class ClientRepositoryImpl implements ClientRepository{

    /**
     * Основное хранилище клиентов.
     * Ключ: уникальный идентификатор клиента (idClient).
     * Значение: объект клиента {@link Client}.
     * Используется {@link ConcurrentHashMap}.
     */
    private final Map<String,Client> storage = new ConcurrentHashMap<>();

    /**
     * Индекс для быстрого поиска клиента по email.
     * Ключ: email клиента (уникальный).
     * Значение: идентификатор клиента (idClient).
     * Поддерживается в актуальном состоянии при сохранении, обновлении и удалении клиентов.
     * Используется {@link ConcurrentHashMap}.
     */
    private final Map<String,String> emailIndexStorage = new ConcurrentHashMap<>();

    /**
     * Индекс для быстрого поиска клиента по комбинации ФИО и даты рождения.
     * Ключ: строка в формате "{fullName}|{birthDate}" (уникальная комбинация).
     * Значение: идентификатор клиента (idClient).
     * Используется {@link ConcurrentHashMap}.
     */
    private final Map<String, String> nameAndBirthIndexStorage = new ConcurrentHashMap<>();

    @Override
    public Client save(Client client) {
        storage.put(client.getId(),client);
        if(client.getEmail() != null)
            emailIndexStorage.put(client.getEmail(),client.getId());
        nameAndBirthIndexStorage.put(client.getFullName() + "|" + client.getBirthDate(),client.getId());
        return client;
    }

    @Override
    public Optional<Client> findById(String id) {
        if(id == null) return Optional.empty();
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Client> findByFullNameAndBirthDate(String fullName, LocalDate birthDate) {
        if(fullName == null || birthDate == null) return Optional.empty();
        return Optional.ofNullable(storage.get(nameAndBirthIndexStorage.get(fullName+"|"+birthDate)));
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        if(email == null) return Optional.empty();
        return Optional.ofNullable(storage.get(emailIndexStorage.get(email)));
    }

    @Override
    public Optional<Client> updateEmail(String clientId, String newEmail){
        Client client = storage.get(clientId);
        if (client == null) return Optional.empty();

        String oldEmail = client.getEmail();
        if(oldEmail != null)
            emailIndexStorage.remove(oldEmail);

        client.setEmail(newEmail);

        if (newEmail != null) {
            emailIndexStorage.put(newEmail, clientId);
        }
        return Optional.of(client);
    }

    @Override
    public List<Client> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteById(String id) {
        Client removed = storage.remove(id);
        if(removed != null){
            if(removed.getEmail() != null)
                emailIndexStorage.remove(removed.getEmail());
            nameAndBirthIndexStorage.remove(removed.getFullName()+"|"+removed.getBirthDate());
            return true;
        }
        return false;
    }

    @Override
    public int count() {
        return storage.size();
    }

    @Override
    public boolean existsById(String id) {
        return storage.containsKey(id);
    }
}
