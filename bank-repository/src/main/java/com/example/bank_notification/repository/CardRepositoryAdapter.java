package com.example.bank_notification.repository;

import com.example.bank_notification.entity.CardEntity;
import com.example.bank_notification.model.BankCard;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "jpa")
@RequiredArgsConstructor
public class CardRepositoryAdapter implements CardRepository{

    private final CardJpaRepository cardJpaRepository;
    private final ClientJpaRepository clientJpaRepository;

    @Override
    @Transactional
    public BankCard save(BankCard card) {
        CardEntity entity = new CardEntity(
                clientJpaRepository.findById(card.getClientId()).orElseThrow(),
                card.getCardNumber(),
                card.getIssueDate(),
                card.getExpiryDate(),
                card.getCardType()
        );
        CardEntity saved = cardJpaRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public Optional<BankCard> findById(String id) {
        return cardJpaRepository.findById(id).map(this::toModel);
    }

    @Override
    public Optional<BankCard> findByCardNumber(String cardNumber) {
        return cardJpaRepository.findByCardNumber(cardNumber).map(this::toModel);
    }

    @Override
    public List<BankCard> findByClientId(String clientId) {
        return cardJpaRepository.findByClientId(clientId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<BankCard> findActiveCards() {
        return cardJpaRepository.findAll().stream()
                .filter(CardEntity::isActive)
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<BankCard> findCardsExpiringExactly(int days) {
        LocalDate target = LocalDate.now().plusDays(days);
        return cardJpaRepository.findByActiveTrueAndExpiryDate(target).stream()
                .map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public List<BankCard> findExpiringCards(int days) {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusDays(days);
        return cardJpaRepository.findActiveCardsExpiringBetween(today,targetDate).stream()
                .map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public List<BankCard> findAll() {
        return cardJpaRepository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteById(String id) {
        if(cardJpaRepository.existsById(id)){
            cardJpaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteByClientId(String clientId) {
        List<CardEntity> cardEntities = cardJpaRepository.findByClientId(clientId);
        if(!cardEntities.isEmpty()){
            cardJpaRepository.deleteAll(cardEntities);
            return true;
        }
        return false;
    }

    @Override
    public boolean existsById(String cardId) {
        return cardJpaRepository.existsById(cardId);
    }

    @Override
    public boolean existsByCardNumber(String cardNumber) {
        return cardJpaRepository.existsByCardNumber(cardNumber);
    }

    @Override
    public long count() {
        return cardJpaRepository.count();
    }

    @Override
    public long countActive() {
        return cardJpaRepository.findAll().stream().filter(CardEntity::isActive).count();
    }

    @Override
    public long countExpiringCards(int days) {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusDays(days);
        return cardJpaRepository.findActiveCardsExpiringBetween(today, targetDate).size();
    }

    private BankCard toModel(CardEntity entity) {
        if (entity == null) return null;

        BankCard card = new BankCard(
                entity.getClient().getId(),
                entity.getCardNumber(),
                entity.getIssueDate(),
                entity.getExpiryDate(),
                entity.getCardType()
        );
        card.setId(entity.getId());
        card.setActive(entity.isActive());
        card.setCreatedAt(entity.getCreatedAt());
        return card;
    }
}
