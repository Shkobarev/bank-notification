package com.example.bank_notification.service.mapper;

import com.example.bank_notification.dto.CardDto;
import com.example.bank_notification.model.BankCard;
import com.example.bank_notification.repository.ClientRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CardMapper {
    private final ClientRepository clientRepository;

    public CardMapper(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Преобразует BankCard в CardDto.
     */
    public CardDto toDto(BankCard card) {
        if (card == null) return null;

        CardDto dto = new CardDto();
        dto.setId(card.getId());
        dto.setCardNumber(card.getCardNumber());
        dto.setClientId(card.getClientId());
        dto.setIssueDate(card.getIssueDate());
        dto.setExpiryDate(card.getExpiryDate());
        dto.setCardType(card.getCardType());
        dto.setActive(card.isActive());
        dto.setExpired(card.isExpired());
        dto.setDaysUntilExpired(card.daysUntilExpired());
        dto.setCreatedAt(card.getCreatedAt());

        clientRepository.findById(card.getClientId())
                .ifPresent(client -> dto.setCardholderName(client.getFullName()));

        return dto;
    }

    /**
     * Преобразует список BankCard в список CardDto.
     */
    public List<CardDto> toDtoList(List<BankCard> cards) {
        return cards.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
