package com.example.bank_notification.repository;

import com.example.bank_notification.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ClientJpaRepository extends JpaRepository<ClientEntity, String> {
    Optional<ClientEntity> findByEmail(String email);

    Optional<ClientEntity> findByFullNameAndBirthDate(String fullName, LocalDate birthDate);
}
