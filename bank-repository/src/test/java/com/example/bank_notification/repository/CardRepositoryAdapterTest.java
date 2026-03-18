package com.example.bank_notification.repository;

import com.example.bank_notification.entity.CardEntity;
import com.example.bank_notification.entity.ClientEntity;
import com.example.bank_notification.model.BankCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CardRepositoryAdapter Tests")
public class CardRepositoryAdapterTest {
    @Mock
    private CardJpaRepository cardJpaRepository;

    @Mock
    private ClientJpaRepository clientJpaRepository;

    @InjectMocks
    private CardRepositoryAdapter adapter;

    @Captor
    private ArgumentCaptor<CardEntity> entityCaptor;

    private ClientEntity testClientEntity;
    private BankCard testCard;
    private CardEntity testCardEntity;

    @BeforeEach
    void setUp() {
        testClientEntity = new ClientEntity(
                "Иван Петров",
                LocalDate.of(1990, 1, 15),
                "ivan@mail.com",
                "1234 567890",
                "+79001234567"
        );
        testClientEntity.setId("client-123");

        testCard = new BankCard(
                "client-123",
                "4532-1234-5678-9123",
                LocalDate.now(),
                LocalDate.now().plusYears(3),
                "VISA"
        );
        testCard.setId("card-123");
        testCard.setActive(true);
        testCard.setCreatedAt(LocalDate.now());

        testCardEntity = new CardEntity(
                testClientEntity,
                "4532-1234-5678-9123",
                LocalDate.now(),
                LocalDate.now().plusYears(3),
                "VISA"
        );
        testCardEntity.setId("card-123");
        testCardEntity.setActive(true);
        testCardEntity.setCreatedAt(LocalDate.now());
    }

    @Nested
    @DisplayName("save() method tests")
    class SaveTests {

        @Test
        @DisplayName("Should save new card")
        void shouldSaveNewCard() {
            when(clientJpaRepository.findById("client-123")).thenReturn(Optional.of(testClientEntity));
            when(cardJpaRepository.save(any(CardEntity.class))).thenReturn(testCardEntity);

            BankCard saved = adapter.save(testCard);

            assertThat(saved).isNotNull();
            assertThat(saved.getId()).isEqualTo("card-123");
            assertThat(saved.getCardNumber()).isEqualTo("4532-1234-5678-9123");

            verify(clientJpaRepository).findById("client-123");
            verify(cardJpaRepository).save(entityCaptor.capture());
            CardEntity captured = entityCaptor.getValue();
            assertThat(captured.getCardNumber()).isEqualTo("4532-1234-5678-9123");
            assertThat(captured.getClient().getId()).isEqualTo("client-123");
        }
    }

    @Nested
    @DisplayName("findById() method tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return card when found")
        void shouldReturnCardWhenFound() {
            when(cardJpaRepository.findById("card-123")).thenReturn(Optional.of(testCardEntity));

            Optional<BankCard> result = adapter.findById("card-123");

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo("card-123");
            assertThat(result.get().getCardNumber()).isEqualTo("4532-1234-5678-9123");
        }

        @Test
        @DisplayName("Should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            when(cardJpaRepository.findById("not-found")).thenReturn(Optional.empty());

            Optional<BankCard> result = adapter.findById("not-found");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByCardNumber() method tests")
    class FindByCardNumberTests {

        @Test
        @DisplayName("Should return card when number found")
        void shouldReturnCardWhenNumberFound() {
            when(cardJpaRepository.findByCardNumber("4532-1234-5678-9123"))
                    .thenReturn(Optional.of(testCardEntity));

            Optional<BankCard> result = adapter.findByCardNumber("4532-1234-5678-9123");

            assertThat(result).isPresent();
            assertThat(result.get().getCardNumber()).isEqualTo("4532-1234-5678-9123");
        }

        @Test
        @DisplayName("Should return empty when number not found")
        void shouldReturnEmptyWhenNumberNotFound() {
            when(cardJpaRepository.findByCardNumber("0000-0000-0000-0000"))
                    .thenReturn(Optional.empty());

            Optional<BankCard> result = adapter.findByCardNumber("0000-0000-0000-0000");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByClientId() method tests")
    class FindByClientIdTests {

        @Test
        @DisplayName("Should return all client cards")
        void shouldReturnAllClientCards() {
            List<CardEntity> entities = List.of(testCardEntity);
            when(cardJpaRepository.findByClientId("client-123")).thenReturn(entities);

            List<BankCard> result = adapter.findByClientId("client-123");

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getClientId()).isEqualTo("client-123");
        }

        @Test
        @DisplayName("Should return empty list when client has no cards")
        void shouldReturnEmptyListWhenNoCards() {
            when(cardJpaRepository.findByClientId("client-123")).thenReturn(List.of());

            List<BankCard> result = adapter.findByClientId("client-123");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findActiveCards() method tests")
    class FindActiveCardsTests {

        @Test
        @DisplayName("Should return only active cards")
        void shouldReturnOnlyActiveCards() {
            CardEntity inactiveCard = new CardEntity(
                    testClientEntity,
                    "5555-6666-7777-8888",
                    LocalDate.now(),
                    LocalDate.now().plusYears(2),
                    "Mastercard"
            );
            inactiveCard.setActive(false);

            List<CardEntity> allCards = List.of(testCardEntity, inactiveCard);
            when(cardJpaRepository.findAll()).thenReturn(allCards);

            List<BankCard> result = adapter.findActiveCards();

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getCardNumber()).isEqualTo("4532-1234-5678-9123");
        }
    }

    @Nested
    @DisplayName("findExpiringCards() method tests")
    class FindExpiringCardsTests {

        @Test
        @DisplayName("Should find cards expiring within days")
        void shouldFindCardsExpiringWithinDays() {
            int days = 30;
            LocalDate today = LocalDate.now();
            LocalDate targetDate = today.plusDays(days);

            List<CardEntity> expiringCards = List.of(testCardEntity);
            when(cardJpaRepository.findActiveCardsExpiringBetween(today, targetDate))
                    .thenReturn(expiringCards);

            List<BankCard> result = adapter.findExpiringCards(days);

            assertThat(result).hasSize(1);
            verify(cardJpaRepository).findActiveCardsExpiringBetween(today, targetDate);
        }
    }

    @Nested
    @DisplayName("findCardsExpiringExactly() method tests")
    class FindCardsExpiringExactlyTests {

        @Test
        @DisplayName("Should find cards expiring exactly on target date")
        void shouldFindCardsExpiringExactly() {
            int days = 30;
            LocalDate target = LocalDate.now().plusDays(days);

            List<CardEntity> exactCards = List.of(testCardEntity);
            when(cardJpaRepository.findByActiveTrueAndExpiryDate(target))
                    .thenReturn(exactCards);

            List<BankCard> result = adapter.findCardsExpiringExactly(days);

            assertThat(result).hasSize(1);
            verify(cardJpaRepository).findByActiveTrueAndExpiryDate(target);
        }
    }

    @Nested
    @DisplayName("delete methods tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete card by ID when exists")
        void shouldDeleteCardByIdWhenExists() {
            when(cardJpaRepository.existsById("card-123")).thenReturn(true);

            boolean result = adapter.deleteById("card-123");

            assertThat(result).isTrue();
            verify(cardJpaRepository).deleteById("card-123");
        }

        @Test
        @DisplayName("Should return false when card not found for delete")
        void shouldReturnFalseWhenCardNotFound() {
            when(cardJpaRepository.existsById("not-found")).thenReturn(false);

            boolean result = adapter.deleteById("not-found");

            assertThat(result).isFalse();
            verify(cardJpaRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Should delete all cards by client ID")
        void shouldDeleteAllCardsByClientId() {
            List<CardEntity> cards = List.of(testCardEntity);
            when(cardJpaRepository.findByClientId("client-123")).thenReturn(cards);

            boolean result = adapter.deleteByClientId("client-123");

            assertThat(result).isTrue();
            verify(cardJpaRepository).deleteAll(cards);
        }

        @Test
        @DisplayName("Should return false when client has no cards")
        void shouldReturnFalseWhenClientHasNoCards() {
            when(cardJpaRepository.findByClientId("client-123")).thenReturn(List.of());

            boolean result = adapter.deleteByClientId("client-123");

            assertThat(result).isFalse();
            verify(cardJpaRepository, never()).deleteAll(any());
        }
    }

    @Nested
    @DisplayName("exists and count methods tests")
    class ExistsAndCountTests {

        @Test
        @DisplayName("Should return true when card exists by ID")
        void shouldReturnTrueWhenCardExists() {
            when(cardJpaRepository.existsById("card-123")).thenReturn(true);

            boolean result = adapter.existsById("card-123");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return true when card number exists")
        void shouldReturnTrueWhenCardNumberExists() {
            when(cardJpaRepository.existsByCardNumber("4532-1234-5678-9123")).thenReturn(true);

            boolean result = adapter.existsByCardNumber("4532-1234-5678-9123");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return correct count")
        void shouldReturnCorrectCount() {
            when(cardJpaRepository.count()).thenReturn(10L);

            long result = adapter.count();

            assertThat(result).isEqualTo(10L);
        }

        @Test
        @DisplayName("Should return correct active count")
        void shouldReturnCorrectActiveCount() {
            CardEntity inactiveCard = new CardEntity(
                    testClientEntity,
                    "5555-6666-7777-8888",
                    LocalDate.now(),
                    LocalDate.now().plusYears(2),
                    "Mastercard"
            );
            inactiveCard.setActive(false);

            List<CardEntity> allCards = List.of(testCardEntity, inactiveCard);
            when(cardJpaRepository.findAll()).thenReturn(allCards);

            long result = adapter.countActive();

            assertThat(result).isEqualTo(1);
        }
    }
}
