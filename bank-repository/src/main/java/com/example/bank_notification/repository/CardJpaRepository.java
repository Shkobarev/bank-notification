package com.example.bank_notification.repository;

import com.example.bank_notification.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardJpaRepository extends JpaRepository<CardEntity, String> {
    List<CardEntity> findByClientId(String clientId);

    Optional<CardEntity> findByCardNumber(String cardNumber);

    boolean existsByCardNumber(String cardNumber);

    List<CardEntity> findByActiveTrueAndExpiryDate(LocalDate date);

    @Query("SELECT c FROM CardEntity c WHERE c.active = true AND c.expiryDate BETWEEN :start AND :end")
    List<CardEntity> findActiveCardsExpiringBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
