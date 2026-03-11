package com.example.bank_notification.service;

import com.example.bank_notification.dto.CardDto;
import com.example.bank_notification.dto.mapper.CardMapper;
import com.example.bank_notification.model.BankCard;
import com.example.bank_notification.repository.CardRepository;
import com.example.bank_notification.repository.ClientRepository;
import com.example.bank_notification.util.CardNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CardServiceImpl Tests")
public class CardServiceImplTest {
    @Mock
    private CardRepository cardRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private CardNumberGenerator cardNumberGenerator;

    @InjectMocks
    private CardServiceImpl cardService;

    private BankCard testCard;
    private CardDto testCardDto;

    @BeforeEach
    void setUp() {
        testCard = new BankCard(
                "client-123",
                "4532-1234-5678-9123",
                LocalDate.now(),
                LocalDate.now().plusYears(3),
                "VISA"
        );
        testCard.setId("card-123");

        testCardDto = new CardDto();
        testCardDto.setId("card-123");
        testCardDto.setCardNumber("4532-1234-5678-9123");
        testCardDto.setClientId("client-123");
        testCardDto.setCardType("VISA");
    }

    @Nested
    @DisplayName("createCard() tests")
    class CreateCardTests {

        @Test
        @DisplayName("Should create card successfully")
        void shouldCreateCardSuccessfully() {
            String clientId = "client-123";
            String cardType = "VISA";
            int validityYears = 3;

            when(clientRepository.existsById(clientId)).thenReturn(true);
            when(cardNumberGenerator.generate(cardType)).thenReturn("4532-1234-5678-9123");
            when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());
            when(cardRepository.save(any(BankCard.class))).thenReturn(testCard);
            when(cardMapper.toDto(any(BankCard.class))).thenReturn(testCardDto);

            CardDto result = cardService.createCard(clientId, cardType, validityYears);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("card-123");
            assertThat(result.getCardType()).isEqualTo("VISA");

            verify(clientRepository).existsById(clientId);
            verify(cardNumberGenerator).generate(cardType);
            verify(cardRepository).save(any(BankCard.class));
            verify(cardMapper).toDto(any(BankCard.class));
        }

        @Test
        @DisplayName("Should throw exception when client not found")
        void shouldThrowExceptionWhenClientNotFound() {
            String clientId = "non-existent";
            when(clientRepository.existsById(clientId)).thenReturn(false);

            assertThatThrownBy(() -> cardService.createCard(clientId, "VISA", 3))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Client not found: " + clientId);
        }

        @Test
        @DisplayName("Should throw exception when validity years invalid")
        void shouldThrowExceptionWhenValidityYearsInvalid() {
            String clientId = "client-123";
            when(clientRepository.existsById(clientId)).thenReturn(true);

            assertThatThrownBy(() -> cardService.createCard(clientId, "VISA", 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Validity years must be between 1 and 5");

            assertThatThrownBy(() -> cardService.createCard(clientId, "VISA", 6))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Validity years must be between 1 and 5");

            assertThatThrownBy(() -> cardService.createCard(clientId, "VISA", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Validity years must be between 1 and 5");
        }

        @Test
        @DisplayName("Should retry generating unique card number")
        void shouldRetryGeneratingUniqueCardNumber() {
            String clientId = "client-123";
            String cardType = "VISA";

            when(clientRepository.existsById(clientId)).thenReturn(true);
            when(cardNumberGenerator.generate(cardType))
                    .thenReturn("1111-1111-1111-1111")
                    .thenReturn("2222-2222-2222-2222")
                    .thenReturn("3333-3333-3333-3333")
                    .thenReturn("4532-1234-5678-9123");

            when(cardRepository.findByCardNumber(anyString()))
                    .thenReturn(Optional.of(new BankCard()))
                    .thenReturn(Optional.of(new BankCard()))
                    .thenReturn(Optional.of(new BankCard()))
                    .thenReturn(Optional.empty());

            when(cardRepository.save(any(BankCard.class))).thenReturn(testCard);
            when(cardMapper.toDto(any(BankCard.class))).thenReturn(testCardDto);

            CardDto result = cardService.createCard(clientId, cardType, 3);

            assertThat(result).isNotNull();
            verify(cardNumberGenerator, times(4)).generate(cardType);
            verify(cardRepository, times(4)).findByCardNumber(anyString());
        }

        @Test
        @DisplayName("Should throw exception after max attempts")
        void shouldThrowExceptionAfterMaxAttempts() {
            String clientId = "client-123";
            String cardType = "VISA";

            when(clientRepository.existsById(clientId)).thenReturn(true);
            when(cardNumberGenerator.generate(cardType)).thenReturn("4532-1234-5678-9123");
            when(cardRepository.findByCardNumber(anyString()))
                    .thenReturn(Optional.of(new BankCard()));

            assertThatThrownBy(() -> cardService.createCard(clientId, cardType, 3))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to generate unique card number");

            verify(cardNumberGenerator, times(10)).generate(cardType);
            verify(cardRepository, times(10)).findByCardNumber(anyString());
            verify(cardRepository, never()).save(any(BankCard.class));
        }
    }

    @Nested
    @DisplayName("getClientCards() tests")
    class GetClientCardsTests {

        @Test
        @DisplayName("Should return list of client cards")
        void shouldReturnListOfClientCards() {
            String clientId = "client-123";
            List<BankCard> cards = List.of(testCard);

            when(cardRepository.findByClientId(clientId)).thenReturn(cards);
            when(cardMapper.toDto(testCard)).thenReturn(testCardDto);

            List<CardDto> result = cardService.getClientCards(clientId);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo("card-123");
        }

        @Test
        @DisplayName("Should return empty list when client has no cards")
        void shouldReturnEmptyListWhenNoCards() {
            String clientId = "client-123";
            when(cardRepository.findByClientId(clientId)).thenReturn(List.of());

            List<CardDto> result = cardService.getClientCards(clientId);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getCard() tests")
    class GetCardTests {

        @Test
        @DisplayName("Should return card when exists")
        void shouldReturnCardWhenExists() {
            String cardId = "card-123";
            when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));
            when(cardMapper.toDto(testCard)).thenReturn(testCardDto);

            Optional<CardDto> result = cardService.getCard(cardId);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo("card-123");
        }

        @Test
        @DisplayName("Should return empty when card not found")
        void shouldReturnEmptyWhenNotFound() {
            String cardId = "non-existent";
            when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

            Optional<CardDto> result = cardService.getCard(cardId);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("cancelCard() tests")
    class CancelCardTests {

        @Test
        @DisplayName("Should cancel active card")
        void shouldCancelActiveCard() {
            String cardId = "card-123";
            testCard.setActive(true);

            when(cardRepository.existsById(cardId)).thenReturn(true);
            when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));
            when(cardRepository.save(any(BankCard.class))).thenReturn(testCard);

            boolean result = cardService.cancelCard(cardId);

            assertThat(result).isTrue();
            assertThat(testCard.isActive()).isFalse();
            verify(cardRepository).save(testCard);
        }

        @Test
        @DisplayName("Should throw exception when card not found")
        void shouldThrowExceptionWhenCardNotFound() {
            String cardId = "non-existent";
            when(cardRepository.existsById(cardId)).thenReturn(false);

            assertThatThrownBy(() -> cardService.cancelCard(cardId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Card not found: " + cardId);
        }

        @Test
        @DisplayName("Should return false when card already inactive")
        void shouldReturnFalseWhenCardAlreadyInactive() {
            String cardId = "card-123";
            testCard.setActive(false);

            when(cardRepository.existsById(cardId)).thenReturn(true);
            when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));

            boolean result = cardService.cancelCard(cardId);

            assertThat(result).isFalse();
            verify(cardRepository, never()).save(any(BankCard.class));
        }
    }

    @Nested
    @DisplayName("getCardsExpiringExactlyIn() tests")
    class GetCardsExpiringExactlyInTests {

        @Test
        @DisplayName("Should return cards expiring exactly in specified days")
        void shouldReturnCardsExpiringExactlyIn() {
            int days = 1;
            List<BankCard> exactCards = List.of(testCard);

            when(cardRepository.findCardsExpiringExactly(days)).thenReturn(exactCards);
            when(cardMapper.toDto(testCard)).thenReturn(testCardDto);

            List<CardDto> result = cardService.getCardsExpiringExactly(days);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo("card-123");

            verify(cardRepository).findCardsExpiringExactly(days);
            verify(cardMapper).toDto(testCard);
        }

        @Test
        @DisplayName("Should throw exception when days negative")
        void shouldThrowExceptionWhenDaysNegative() {
            assertThatThrownBy(() -> cardService.getCardsExpiringExactly(-5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Days cannot be negative");
        }
    }

    @Nested
    @DisplayName("getExpiringCards() tests")
    class GetExpiringCardsTests {

        @Test
        @DisplayName("Should return expiring cards")
        void shouldReturnExpiringCards() {
            int days = 30;
            List<BankCard> expiringCards = List.of(testCard);

            when(cardRepository.findExpiringCards(days)).thenReturn(expiringCards);
            when(cardMapper.toDto(testCard)).thenReturn(testCardDto);

            List<CardDto> result = cardService.getExpiringCards(days);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should throw exception when days negative")
        void shouldThrowExceptionWhenDaysNegative() {
            assertThatThrownBy(() -> cardService.getExpiringCards(-5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Days cannot be negative");
        }
    }

    @Nested
    @DisplayName("getActiveClientCards() tests")
    class GetActiveClientCardsTests {

        @Test
        @DisplayName("Should return active cards only")
        void shouldReturnActiveCardsOnly() {
            String clientId = "client-123";
            BankCard inactiveCard = new BankCard();
            inactiveCard.setId("card-456");
            inactiveCard.setActive(false);

            List<BankCard> cards = List.of(testCard, inactiveCard);

            when(clientRepository.existsById(clientId)).thenReturn(true);
            when(cardRepository.findByClientId(clientId)).thenReturn(cards);
            when(cardMapper.toDto(testCard)).thenReturn(testCardDto);

            List<CardDto> result = cardService.getActiveClientCards(clientId);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo("card-123");
        }

        @Test
        @DisplayName("Should throw exception when client not found")
        void shouldThrowExceptionWhenClientNotFound() {
            String clientId = "non-existent";
            when(clientRepository.existsById(clientId)).thenReturn(false);

            assertThatThrownBy(() -> cardService.getActiveClientCards(clientId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Client not found: " + clientId);
        }
    }

    @Nested
    @DisplayName("cardExists() tests")
    class CardExistsTests {

        @Test
        @DisplayName("Should return true when card exists")
        void shouldReturnTrueWhenCardExists() {
            String cardId = "card-123";
            when(cardRepository.existsById(cardId)).thenReturn(true);

            boolean result = cardService.cardExists(cardId);

            assertThat(result).isTrue();
            verify(cardRepository).existsById(cardId);
            verifyNoMoreInteractions(cardRepository);
        }

        @Test
        @DisplayName("Should return false when card does not exist")
        void shouldReturnFalseWhenCardDoesNotExist() {
            String cardId = "non-existent";
            when(cardRepository.existsById(cardId)).thenReturn(false);

            boolean result = cardService.cardExists(cardId);

            assertThat(result).isFalse();
            verify(cardRepository).existsById(cardId);
            verifyNoMoreInteractions(cardRepository);
        }
    }

    @Nested
    @DisplayName("isCardNumberUnique() tests")
    class IsCardNumberUniqueTests {

        @Test
        @DisplayName("Should return true when card number is unique")
        void shouldReturnTrueWhenCardNumberIsUnique() {
            String cardNumber = "4532-1234-5678-9123";
            when(cardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.empty());

            boolean result = cardService.isCardNumberUnique(cardNumber);

            assertThat(result).isTrue();
            verify(cardRepository).findByCardNumber(cardNumber);
            verifyNoMoreInteractions(cardRepository);
        }

        @Test
        @DisplayName("Should return false when card number already exists")
        void shouldReturnFalseWhenCardNumberAlreadyExists() {
            String cardNumber = "4532-1234-5678-9123";
            BankCard existingCard = new BankCard();
            existingCard.setId("card-123");
            existingCard.setCardNumber(cardNumber);

            when(cardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(existingCard));

            boolean result = cardService.isCardNumberUnique(cardNumber);

            assertThat(result).isFalse();
            verify(cardRepository).findByCardNumber(cardNumber);
        }
    }
}
