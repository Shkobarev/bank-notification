package com.example.bank_notification.repository;

import com.example.bank_notification.model.BankCard;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CardRepository {

    BankCard save(BankCard card);

    Optional<BankCard> findById(String id);

    Optional<BankCard> findByCardNumber(String cardNumber);

    List<BankCard> findByClientId(String clientId);

    List<BankCard> findActiveCards();

    List<BankCard> findExpiringCards(int days);

    List<BankCard> findAll();

    boolean deleteById(String id);

    boolean deleteByClientId(String clientId);

    boolean existsByCardNumber(String cardNumber);

    int count();

    long countActive();

    long countExpiringCards(int days);
}
