package com.example.bank_notification.repository;

import com.example.bank_notification.model.Client;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClientRepository {

    Client save(Client client);

    Optional<Client> findById(String id);

    Optional<Client> findByFullNameAndBirthDate(String fullName, LocalDate birthDate);

    Optional<Client> findByEmail(String email);

    List<Client> findAll();

    boolean deleteById(String id);

    int count();

    boolean existsById(String id);
}
