package com.example.bank_notification.repository;

import com.example.bank_notification.model.BankCard;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Реализация репозитория для работы с банковскими картами.
 * Хранит данные в оперативной памяти с использованием индексов для быстрого поиска.
 */
@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "memory", matchIfMissing = true)
public class CardRepositoryImpl implements CardRepository{

    /**
     * Основное хранилище карт.
     * Ключ: уникальный идентификатор карты (String)
     * Значение: объект банковской карты (BankCard)
     * Используется {@link ConcurrentHashMap}.
     */
    private final Map<String,BankCard> storage = new ConcurrentHashMap<>();

    /**
     * Индекс для поиска по номеру карты.
     * Ключ: номер карты (String) - уникален в системе
     * Значение: идентификатор карты (String)
     * Используется {@link ConcurrentHashMap}.
     */
    private final Map<String,String> cardNumberIndexStorage = new ConcurrentHashMap<>();

    /**
     * Индекс для поиска карт по клиенту.
     * Ключ: идентификатор клиента (String)
     * Значение: список идентификаторов карт клиента (List&lt;String&gt;)
     * Используется {@link ConcurrentHashMap}.
     */
    private final Map<String,List<String>> clientCardsIndexStorage = new ConcurrentHashMap<>();

    @Override
    public BankCard save(BankCard card) {
        storage.put(card.getId(),card);
        cardNumberIndexStorage.put(card.getCardNumber(),card.getId());
        clientCardsIndexStorage.computeIfAbsent(card.getClientId(),
                k -> new CopyOnWriteArrayList<>())
                .add(card.getId());
        return card;
    }

    @Override
    public Optional<BankCard> findById(String id) {
        if(id == null) return Optional.empty();
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<BankCard> findByCardNumber(String cardNumber) {
        if(cardNumber == null) return Optional.empty();
        String cardId = cardNumberIndexStorage.get(cardNumber);
        if(cardId == null) return Optional.empty();
        return Optional.ofNullable(storage.get(cardId));
    }

    @Override
    public List<BankCard> findByClientId(String clientId) {
        if (clientId == null) return new ArrayList<>();
        List<String> idCards = clientCardsIndexStorage.getOrDefault(clientId,Collections.emptyList());
        return idCards.stream()
                .map(storage::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<BankCard> findActiveCards() {
        return storage.values().stream()
                .filter(BankCard::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public List<BankCard> findCardsExpiringExactly(int days){
        if (days < 0) return Collections.emptyList();
        return storage.values().stream()
                .filter(BankCard::isActive)
                .filter(c->!c.isExpired())
                .filter(c->c.daysUntilExpired() == days)
                .collect(Collectors.toList());
    }

    @Override
    public List<BankCard> findExpiringCards(int days) {
        if (days < 0) return Collections.emptyList();
        return storage.values().stream()
                .filter(BankCard::isActive)
                .filter(c->!c.isExpired())
                .filter(c->c.daysUntilExpired() <= days)
                .collect(Collectors.toList());
    }

    @Override
    public List<BankCard> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteById(String id) {
        if(id == null) return false;
        BankCard removed = storage.remove(id);
        if(removed != null){
            cardNumberIndexStorage.remove(removed.getCardNumber());
            List<String> cards = clientCardsIndexStorage.get(removed.getClientId());
            if(cards != null){
                cards.remove(id);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByClientId(String clientId) {
        if(clientId == null) return false;
        List<String> removed = clientCardsIndexStorage.remove(clientId);
        if(removed != null){
            for(String cardId: removed){
                cardNumberIndexStorage.remove(storage.get(cardId).getCardNumber());
                storage.remove(cardId);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean existsById(String cardId){
        if(cardId == null) return false;
        return storage.containsKey(cardId);
    }

    @Override
    public boolean existsByCardNumber(String cardNumber) {
        if(cardNumber == null) return false;
        return cardNumberIndexStorage.containsKey(cardNumber);
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public long countActive() {
        return storage.values().stream()
                .filter(BankCard::isActive)
                .count();
    }

    @Override
    public long countExpiringCards(int days) {
        return findExpiringCards(days).size();
    }
}
