package com.example.bank_notification.controller;

import com.example.bank_notification.dto.CardDto;
import com.example.bank_notification.dto.request.CreateCardRequest;
import com.example.bank_notification.service.CardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(CardController.class)
@DisplayName("CardController Tests")
public class CardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CardService cardService;

    private final String clientId = "550e8400-e29b-41d4-a716-446655440000";
    private final String cardId = "660f9500-f30c-52e5-b827-557766551111";

    private CardDto createTestCardDto() {
        CardDto dto = new CardDto();
        dto.setId(cardId);
        dto.setCardNumber("****-****-****-1234");
        dto.setClientId(clientId);
        dto.setIssueDate(LocalDate.now());
        dto.setExpiryDate(LocalDate.now().plusYears(3));
        dto.setCardType("VISA");
        dto.setActive(true);
        dto.setDaysUntilExpired(365 * 3);
        return dto;
    }

    @Nested
    @DisplayName("POST /api/clients/{clientId}/cards")
    class  CreateCardTests {

        @Test
        @DisplayName("Should return 201 when card created")
        void shouldReturn201WhenCardCreated() throws Exception {
            CreateCardRequest request = new CreateCardRequest();
            request.setCardType("VISA");
            request.setValidityYears(3);

            when(cardService.createCard(eq(clientId), eq("VISA"), eq(3)))
                    .thenReturn(createTestCardDto());

            mockMvc.perform(post("/api/clients/{clientId}/cards", clientId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(cardId))
                    .andExpect(jsonPath("$.cardType").value("VISA"));
        }

        @Test
        @DisplayName("Should return 400 when validation fails")
        void shouldReturn400WhenValidationFails() throws Exception {
            CreateCardRequest request = new CreateCardRequest();

            mockMvc.perform(post("/api/clients/{clientId}/cards", clientId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/clients/{clientId}/cards")
    class GetClientCardsTests {

        @Test
        @DisplayName("Should return 200 with list of cards")
        void shouldReturn200WithList() throws Exception {
            CardDto card1 = createTestCardDto();
            CardDto card2 = createTestCardDto();
            card2.setId("770g9600-g40d-63f6-c938-668877662222");
            card2.setCardType("Mastercard");

            when(cardService.getClientCards(clientId)).thenReturn(List.of(card1, card2));

            mockMvc.perform(get("/api/clients/{clientId}/cards", clientId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(cardId))
                    .andExpect(jsonPath("$[1].id").value("770g9600-g40d-63f6-c938-668877662222"))
                    .andExpect(jsonPath("$[0].cardType").value("VISA"))
                    .andExpect(jsonPath("$[1].cardType").value("Mastercard"));
        }

        @Test
        @DisplayName("Should return 200 with empty list when no cards")
        void shouldReturn200WithEmptyList() throws Exception {
            when(cardService.getClientCards(clientId)).thenReturn(List.of());

            mockMvc.perform(get("/api/clients/{clientId}/cards", clientId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/cards/{cardId}")
    class GetCardByIdTests {

        @Test
        @DisplayName("Should return 200 when card exists")
        void shouldReturn200WhenCardExists() throws Exception {
            when(cardService.getCard(cardId)).thenReturn(Optional.of(createTestCardDto()));

            mockMvc.perform(get("/api/cards/{cardId}", cardId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(cardId))
                    .andExpect(jsonPath("$.cardType").value("VISA"));
        }

        @Test
        @DisplayName("Should return 404 when card not found")
        void shouldReturn404WhenCardNotFound() throws Exception {
            when(cardService.getCard("not-found")).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/cards/{cardId}", "not-found"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/cards/{cardId}")
    class CancelCardTests {

        @Test
        @DisplayName("Should return 204 when card cancelled")
        void shouldReturn204WhenCardCancelled() throws Exception {
            when(cardService.cancelCard(cardId)).thenReturn(true);

            mockMvc.perform(delete("/api/cards/{cardId}", cardId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should return 404 when card not found")
        void shouldReturn404WhenCardNotFound() throws Exception {
            when(cardService.cancelCard("not-found")).thenReturn(false);

            mockMvc.perform(delete("/api/cards/{cardId}", "not-found"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/cards/expiring")
    class GetExpiringCardsTests {

        @Test
        @DisplayName("Should return 200 with expiring cards")
        void shouldReturn200WithExpiringCards() throws Exception {
            CardDto card1 = createTestCardDto();
            card1.setDaysUntilExpired(30);
            CardDto card2 = createTestCardDto();
            card2.setId("770g9600-g40d-63f6-c938-668877662222");
            card2.setDaysUntilExpired(14);

            when(cardService.getExpiringCards(30)).thenReturn(List.of(card1, card2));

            mockMvc.perform(get("/api/cards/expiring")
                            .param("days", "30"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(cardId))
                    .andExpect(jsonPath("$[1].id").value("770g9600-g40d-63f6-c938-668877662222"));
        }
    }

    @Nested
    @DisplayName("GET /api/clients/{clientId}/cards/active")
    class GetActiveClientCardsTests {

        @Test
        @DisplayName("Should return 200 with active cards")
        void shouldReturn200WithActiveCards() throws Exception {
            CardDto card1 = createTestCardDto();
            CardDto card2 = createTestCardDto();
            card2.setId("770g9600-g40d-63f6-c938-668877662222");
            card2.setActive(true);

            when(cardService.getActiveClientCards(clientId)).thenReturn(List.of(card1, card2));

            mockMvc.perform(get("/api/clients/{clientId}/cards/active", clientId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(cardId))
                    .andExpect(jsonPath("$[1].id").value("770g9600-g40d-63f6-c938-668877662222"));
        }

        @Test
        @DisplayName("Should return 200 with empty list when no active cards")
        void shouldReturn200WithEmptyList() throws Exception {
            when(cardService.getActiveClientCards(clientId)).thenReturn(List.of());

            mockMvc.perform(get("/api/clients/{clientId}/cards/active", clientId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/cards/{cardId}/exists")
    class CardExistsTests {

        @Test
        @DisplayName("Should return true when card exists")
        void shouldReturnTrueWhenCardExists() throws Exception {
            when(cardService.cardExists(cardId)).thenReturn(true);

            mockMvc.perform(get("/api/cards/{cardId}/exists", cardId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @DisplayName("Should return false when card does not exist")
        void shouldReturnFalseWhenCardDoesNotExist() throws Exception {
            when(cardService.cardExists("not-found")).thenReturn(false);

            mockMvc.perform(get("/api/cards/{cardId}/exists", "not-found"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));
        }
    }
}
