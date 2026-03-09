package com.example.bank_notification.service;

import com.example.bank_notification.dto.CardDto;
import com.example.bank_notification.dto.mapper.CardMapper;
import com.example.bank_notification.model.BankCard;
import com.example.bank_notification.repository.CardRepository;
import com.example.bank_notification.repository.ClientRepository;
import com.example.bank_notification.util.CardNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService{

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final ClientRepository clientRepository;
    private final CardNumberGenerator cardNumberGenerator;

    @Override
    public CardDto createCard(String clientId, String cardType, Integer validityYears) {
        if (!clientRepository.existsById(clientId)) {
            throw new IllegalArgumentException("Client not found: " + clientId);
        }
        if (validityYears == null || validityYears < 1 || validityYears > 5) {
            throw new IllegalArgumentException("Validity years must be between 1 and 5");
        }

        String cardNumber = generateUniqueCardNumber(cardType);

        LocalDate issueDate = LocalDate.now();
        LocalDate expiryDate = issueDate.plusYears(validityYears);

        BankCard card = new BankCard(clientId, cardNumber, issueDate, expiryDate, cardType);

        BankCard saved = cardRepository.save(card);

        return cardMapper.toDto(saved);
    }

    private String generateUniqueCardNumber(String cardType){
        int maxAttempts = 10;
        for (int i = 0; i < maxAttempts; i++) {
            String cardNumber = cardNumberGenerator.generate(cardType);
            if (isCardNumberUnique(cardNumber)) {
                return cardNumber;
            }
        }
        throw new RuntimeException("Failed to generate unique card number for type: " + cardType);
    }

    @Override
    public List<CardDto> getClientCards(String clientId) {
        return cardRepository.findByClientId(clientId).stream()
                .map(cardMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CardDto> getCard(String cardId) {
        return cardRepository.findById(cardId).map(cardMapper::toDto);
    }

    @Override
    public boolean cancelCard(String cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new IllegalArgumentException("Card not found: " + cardId);
        }

        Optional<BankCard> cardOptional = cardRepository.findById(cardId);
        if(cardOptional.isPresent()){
            BankCard card = cardOptional.get();

            if(!card.isActive()) return false;

            card.setActive(false);
            cardRepository.save(card);
            return true;
        }
        return false;
    }

    @Override
    public List<CardDto> getExpiringCards(int days) {
        if (days < 0) {
            throw new IllegalArgumentException("Days cannot be negative");
        }

        return cardRepository.findExpiringCards(days).stream()
                .map(cardMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CardDto> getActiveClientCards(String clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new IllegalArgumentException("Client not found: " + clientId);
        }

        return cardRepository.findByClientId(clientId).stream()
                .filter(BankCard::isActive)
                .map(cardMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean cardExists(String cardId) {
        return cardRepository.existsById(cardId);
    }

    @Override
    public boolean isCardNumberUnique(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber).isEmpty();
    }
}
