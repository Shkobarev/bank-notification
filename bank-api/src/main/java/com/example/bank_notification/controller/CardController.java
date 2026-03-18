package com.example.bank_notification.controller;

import com.example.bank_notification.dto.CardDto;
import com.example.bank_notification.dto.request.CreateCardRequest;
import com.example.bank_notification.exception.ResourceNotFoundException;
import com.example.bank_notification.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CardController {
    private final CardService cardService;

    @PostMapping("/clients/{clientId}/cards")
    public ResponseEntity<CardDto> createCard(
            @PathVariable String clientId,
            @Valid @RequestBody CreateCardRequest request) {
        CardDto created = cardService.createCard(
                clientId,
                request.getCardType(),
                request.getValidityYears()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/clients/{clientId}/cards")
    public List<CardDto> getClientCards(@PathVariable String clientId) {
        return cardService.getClientCards(clientId);
    }

    @GetMapping("/cards/{cardId}")
    public CardDto getCard(@PathVariable String cardId) {
        return cardService.getCard(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));
    }

    @DeleteMapping("/cards/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelCard(@PathVariable String cardId) {
        boolean cancelled = cardService.cancelCard(cardId);
        if (!cancelled) {
            throw new ResourceNotFoundException("Card not found with id: " + cardId);
        }
    }

    @GetMapping("/cards/expiring")
    public List<CardDto> getExpiringCards(
            @RequestParam(defaultValue = "30") int days) {
        return cardService.getExpiringCards(days);
    }

    @GetMapping("/clients/{clientId}/cards/active")
    public List<CardDto> getActiveClientCards(@PathVariable String clientId) {
        return cardService.getActiveClientCards(clientId);
    }

    @GetMapping("/cards/{cardId}/exists")
    public Boolean cardExists(@PathVariable String cardId) {
        return cardService.cardExists(cardId);
    }
}
