package com.example.bank_notification.repository;


import com.example.bank_notification.model.BankCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CardRepositoryImplTest {

    private CardRepositoryImpl repository;

    private BankCard activeCard;
    private BankCard inactiveCard;
    private BankCard expiringCard;
    private BankCard expiredCard;

    @BeforeEach
    void setUp() {
        repository = new CardRepositoryImpl();

        activeCard = new BankCard(
                "client-001",
                "1234-5678-9012-3456",
                LocalDate.now().minusMonths(6),
                LocalDate.now().plusYears(2),
                "VISA"
        );
        activeCard.setId("card-001");
        activeCard.setActive(true);

        inactiveCard = new BankCard(
                "client-001",
                "9876-5432-1098-7654",
                LocalDate.now().minusYears(1),
                LocalDate.now().plusYears(3),
                "Mastercard"
        );
        inactiveCard.setId("card-002");
        inactiveCard.setActive(false);

        expiringCard = new BankCard(
                "client-002",
                "5555-6666-7777-8888",
                LocalDate.now().minusYears(2),
                LocalDate.now().plusDays(15),
                "MIR"
        );
        expiringCard.setId("card-003");
        expiringCard.setActive(true);

        expiredCard = new BankCard(
                "client-003",
                "9999-8888-7777-6666",
                LocalDate.now().minusYears(3),
                LocalDate.now().minusDays(15),
                "Mastercard"
        );
        expiredCard.setId("card-004");
        expiredCard.setActive(true);
    }

    @Nested
    @DisplayName("save() method tests")
    class SaveTests {

        @Test
        @DisplayName("Should save card with all fields")
        void shouldSaveCardWithAllFields() {
            BankCard saved = repository.save(activeCard);

            assertThat(saved).isNotNull();
            assertThat(saved.getId()).isEqualTo("card-001");
            assertThat(saved.getCardNumber()).isEqualTo("1234-5678-9012-3456");
            assertThat(saved.getClientId()).isEqualTo("client-001");
            assertThat(saved.getIssueDate()).isEqualTo(LocalDate.now().minusMonths(6));
            assertThat(saved.getExpiryDate()).isEqualTo(LocalDate.now().plusYears(2));
            assertThat(saved.getCardType()).isEqualTo("VISA");
            assertThat(saved.isActive()).isTrue();
            assertThat(saved.getCreatedAt()).isEqualTo(LocalDate.now());
        }

        @Test
        @DisplayName("Should save inactive card")
        void shouldSaveInactiveCard() {
            BankCard saved = repository.save(inactiveCard);

            assertThat(saved).isNotNull();
            assertThat(saved.isActive()).isFalse();
        }

        @Test
        @DisplayName("Should save expired card")
        void shouldSaveExpiredCard() {
            BankCard saved = repository.save(expiredCard);

            assertThat(saved).isNotNull();
            assertThat(saved.isExpired()).isTrue();
            assertThat(saved.daysUntilExpired()).isZero();
        }

        @Test
        @DisplayName("Should update existing card")
        void shouldUpdateExistingCard() {
            repository.save(activeCard);
            activeCard.setCardType("Mastercard");
            activeCard.setActive(false);

            BankCard updated = repository.save(activeCard);

            assertThat(updated.getCardType()).isEqualTo("Mastercard");
            assertThat(updated.isActive()).isFalse();

            Optional<BankCard> found = repository.findById("card-001");
            assertThat(found).isPresent();
            assertThat(found.get().getCardType()).isEqualTo("Mastercard");
            assertThat(found.get().isActive()).isFalse();
        }

        @Test
        @DisplayName("Should throw exception when saving null card")
        void shouldThrowExceptionWhenSavingNull() {
            assertThatThrownBy(() -> repository.save(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("findById() method tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find card by existing ID")
        void shouldFindCardByExistingId() {
            repository.save(activeCard);

            Optional<BankCard> found = repository.findById("card-001");

            assertThat(found).isPresent();
            assertThat(found.get().getCardNumber()).isEqualTo("1234-5678-9012-3456");
        }

        @Test
        @DisplayName("Should return empty Optional when ID not found")
        void shouldReturnEmptyWhenIdNotFound() {
            Optional<BankCard> found = repository.findById("non-existent-id");

            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should return empty Optional when ID is null")
        void shouldReturnEmptyWhenIdIsNull() {
            Optional<BankCard> found = repository.findById(null);

            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByCardNumber() method tests")
    class FindByCardNumberTests {

        @Test
        @DisplayName("Should find card by existing card number")
        void shouldFindCardByExistingNumber() {
            repository.save(activeCard);

            Optional<BankCard> found = repository.findByCardNumber("1234-5678-9012-3456");

            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo("card-001");
        }

        @Test
        @DisplayName("Should return empty Optional when card number not found")
        void shouldReturnEmptyWhenCardNumberNotFound() {
            repository.save(activeCard);

            Optional<BankCard> found = repository.findByCardNumber("0000-0000-0000-0000");

            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should return empty Optional when card number is null")
        void shouldReturnEmptyWhenCardNumberIsNull() {
            Optional<BankCard> found = repository.findByCardNumber(null);

            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByClientId() method tests")
    class FindByClientIdTests {

        @Test
        @DisplayName("Should find all cards for existing client")
        void shouldFindAllCardsForClient() {
            repository.save(activeCard);
            repository.save(inactiveCard);
            repository.save(expiringCard);

            List<BankCard> clientCards = repository.findByClientId("client-001");

            assertThat(clientCards).hasSize(2);
            assertThat(clientCards).extracting(BankCard::getId)
                    .containsExactlyInAnyOrder("card-001", "card-002");
        }

        @Test
        @DisplayName("Should return empty list when client has no cards")
        void shouldReturnEmptyListWhenClientHasNoCards() {
            repository.save(activeCard);

            List<BankCard> clientCards = repository.findByClientId("client-without-cards");

            assertThat(clientCards).isEmpty();
        }

        @Test
        @DisplayName("Should return empty list when clientId is null")
        void shouldReturnEmptyListWhenClientIdIsNull() {
            repository.save(activeCard);

            List<BankCard> clientCards = repository.findByClientId(null);

            assertThat(clientCards).isEmpty();
        }

        @Test
        @DisplayName("Should return cards in consistent order")
        void shouldReturnCardsInConsistentOrder() {
            repository.save(activeCard);
            repository.save(inactiveCard);

            List<BankCard> firstCall = repository.findByClientId("client-001");
            List<BankCard> secondCall = repository.findByClientId("client-001");

            assertThat(firstCall).hasSize(2);
            assertThat(secondCall).hasSize(2);

            assertThat(firstCall).containsExactlyInAnyOrderElementsOf(secondCall);
        }

        @Test
        @DisplayName("Should return copy of list, not original reference")
        void shouldReturnCopyOfList() {
            repository.save(activeCard);
            List<BankCard> found = repository.findByClientId("client-001");

            found.clear();

            assertThat(repository.findByClientId("client-001")).hasSize(1);
        }
    }

    @Nested
    @DisplayName("findActiveCards() method tests")
    class FindActiveCardsTests {

        @Test
        @DisplayName("Should find only active cards")
        void shouldFindOnlyActiveCards() {
            repository.save(activeCard);  //active
            repository.save(inactiveCard); //inactive
            repository.save(expiringCard); //active
            repository.save(expiredCard); //active

            List<BankCard> activeCards = repository.findActiveCards();

            assertThat(activeCards).hasSize(3);
            assertThat(activeCards).extracting(BankCard::getId)
                    .containsExactlyInAnyOrder("card-001", "card-003", "card-004");
        }

        @Test
        @DisplayName("Should return empty list when no active cards")
        void shouldReturnEmptyListWhenNoActiveCards() {
            repository.save(inactiveCard);

            List<BankCard> activeCards = repository.findActiveCards();

            assertThat(activeCards).isEmpty();
        }

        @Test
        @DisplayName("Should return empty list when storage is empty")
        void shouldReturnEmptyListWhenStorageIsEmpty() {
            List<BankCard> activeCards = repository.findActiveCards();

            assertThat(activeCards).isEmpty();
        }
    }

    @Nested
    @DisplayName("findExpiringCards() method tests")
    class FindExpiringCardsTests {

        @Test
        @DisplayName("Should find cards expiring within specified days")
        void shouldFindCardsExpiringWithinDays() {
            repository.save(activeCard);
            repository.save(expiringCard);  // expires in 15 days
            repository.save(expiredCard);

            List<BankCard> expiringIn2Days = repository.findExpiringCards(2);
            List<BankCard> expiringIn30Days = repository.findExpiringCards(30);

            assertThat(expiringIn2Days).hasSize(0);

            assertThat(expiringIn30Days).hasSize(1);
            assertThat(expiringIn30Days).extracting(BankCard::getId)
                    .containsExactlyInAnyOrder("card-003");
        }

        @Test
        @DisplayName("Should include cards expiring exactly today")
        void shouldIncludeCardsExpiringToday() {
            BankCard expiresToday = new BankCard(
                    "client-test",
                    "0000-0000-0000-0000",
                    LocalDate.now().minusYears(3),
                    LocalDate.now(),
                    "VISA"
            );
            expiresToday.setId("card-today");
            repository.save(expiresToday);

            List<BankCard> expiringToday = repository.findExpiringCards(0);

            // Assert
            assertThat(expiringToday).hasSize(1);
            assertThat(expiringToday.getFirst().getId()).isEqualTo("card-today");
        }

        @Test
        @DisplayName("Should return empty list when no cards expire within days")
        void shouldReturnEmptyListWhenNoCardsExpire() {
            repository.save(activeCard);

            List<BankCard> expiringCards = repository.findExpiringCards(30);

            assertThat(expiringCards).isEmpty();
        }

        @Test
        @DisplayName("Should not include expired cards")
        void shouldNotIncludeExpiredCards() {
            repository.save(expiredCard);
            repository.save(expiringCard);

            List<BankCard> expiringCards = repository.findExpiringCards(30);

            assertThat(expiringCards).hasSize(1);
            assertThat(expiringCards.getFirst().getId()).isEqualTo("card-003");
        }
    }

    @Nested
    @DisplayName("findAll() method tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return all cards")
        void shouldReturnAllCards() {
            repository.save(activeCard);
            repository.save(inactiveCard);
            repository.save(expiringCard);
            repository.save(expiredCard);

            List<BankCard> allCards = repository.findAll();

            assertThat(allCards).hasSize(4);
            assertThat(allCards).extracting(BankCard::getId)
                    .containsExactlyInAnyOrder("card-001", "card-002", "card-003", "card-004");
        }

        @Test
        @DisplayName("Should return empty list when no cards exist")
        void shouldReturnEmptyListWhenNoCards() {
            List<BankCard> allCards = repository.findAll();

            assertThat(allCards).isEmpty();
        }

        @Test
        @DisplayName("Should return a copy of data, not the original reference")
        void shouldReturnCopyOfData() {
            repository.save(activeCard);
            List<BankCard> allCards = repository.findAll();

            allCards.clear();

            assertThat(repository.findAll()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("deleteById() method tests")
    class DeleteByIdTests {

        @Test
        @DisplayName("Should delete card by ID")
        void shouldDeleteCardById() {
            repository.save(activeCard);

            boolean deleted = repository.deleteById("card-001");

            assertThat(deleted).isTrue();
            assertThat(repository.findById("card-001")).isEmpty();
        }

        @Test
        @DisplayName("Should remove all indexes when deleting card")
        void shouldRemoveAllIndexesWhenDeletingCard() {
            repository.save(activeCard);

            repository.deleteById("card-001");

            assertThat(repository.findByCardNumber("1234-5678-9012-3456")).isEmpty();
            assertThat(repository.findByClientId("client-001")).isEmpty();
        }

        @Test
        @DisplayName("Should return false when deleting non-existent card")
        void shouldReturnFalseWhenDeletingNonExistentCard() {
            boolean deleted = repository.deleteById("non-existent");

            assertThat(deleted).isFalse();
        }

        @Test
        @DisplayName("Should return false when ID is null")
        void shouldReturnFalseWhenIdIsNull() {
            boolean deleted = repository.deleteById(null);

            assertThat(deleted).isFalse();
        }

        @Test
        @DisplayName("Should handle deleting card that is the only one for client")
        void shouldHandleDeletingLastCardForClient() {
            repository.save(expiringCard);

            repository.deleteById("card-003");

            assertThat(repository.findByClientId("client-002")).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteByClientId() method tests")
    class DeleteByClientIdTests {

        @Test
        @DisplayName("Should delete all cards for client")
        void shouldDeleteAllCardsForClient() {
            repository.save(activeCard);
            repository.save(inactiveCard);

            boolean deleted = repository.deleteByClientId("client-001");

            assertThat(deleted).isTrue();
            assertThat(repository.findByClientId("client-001")).isEmpty();
            assertThat(repository.findById("card-001")).isEmpty();
            assertThat(repository.findById("card-002")).isEmpty();
        }

        @Test
        @DisplayName("Should return false when client has no cards")
        void shouldReturnFalseWhenClientHasNoCards() {
            boolean deleted = repository.deleteByClientId("client-without-cards");

            assertThat(deleted).isFalse();
        }

        @Test
        @DisplayName("Should return false when clientId is null")
        void shouldReturnFalseWhenClientIdIsNull() {
            boolean deleted = repository.deleteByClientId(null);

            assertThat(deleted).isFalse();
        }

        @Test
        @DisplayName("Should handle client with single card correctly")
        void shouldHandleClientWithSingleCard() {
            repository.save(expiringCard);

            boolean deleted = repository.deleteByClientId("client-002");

            assertThat(deleted).isTrue();
            assertThat(repository.findByClientId("client-002")).isEmpty();
            assertThat(repository.count()).isZero();
        }

        @Test
        @DisplayName("Should remove all indexes for deleted cards")
        void shouldRemoveAllIndexesForDeletedCards() {
            repository.save(activeCard);
            repository.save(inactiveCard);

            repository.deleteByClientId("client-001");

            assertThat(repository.findByCardNumber("1234-5678-9012-3456")).isEmpty();
            assertThat(repository.findByCardNumber("9876-5432-1098-7654")).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsById() method tests")
    class ExistsByIdTests{

        @Test
        @DisplayName("Should return true for existing card ID")
        void shouldReturnTrueForExistingCardId() {
            repository.save(activeCard);

            boolean exists = repository.existsById("card-001");

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Should return false for non-existent card ID")
        void shouldReturnFalseForNonExistentCardId() {
            boolean exists = repository.existsById("non-existent-id");

            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("Should return false for null ID")
        void shouldReturnFalseForNullId() {
            boolean exists = repository.existsById(null);

            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("Should return false for deleted card")
        void shouldReturnFalseForDeletedCard() {
            repository.save(activeCard);
            repository.deleteById("card-001");

            boolean exists = repository.existsById("card-001");

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByCardNumber() method tests")
    class ExistsByCardNumberTests {

        @Test
        @DisplayName("Should return true for existing card number")
        void shouldReturnTrueForExistingCardNumber() {
            repository.save(activeCard);

            assertThat(repository.existsByCardNumber("1234-5678-9012-3456")).isTrue();
        }

        @Test
        @DisplayName("Should return false for non-existent card number")
        void shouldReturnFalseForNonExistentCardNumber() {
            assertThat(repository.existsByCardNumber("0000-0000-0000-0000")).isFalse();
        }

        @Test
        @DisplayName("Should return false for null card number")
        void shouldReturnFalseForNullCardNumber() {
            assertThat(repository.existsByCardNumber(null)).isFalse();
        }

        @Test
        @DisplayName("Should return false for deleted card")
        void shouldReturnFalseForDeletedCard() {
            repository.save(activeCard);
            repository.deleteById("card-001");

            assertThat(repository.existsByCardNumber("1234-5678-9012-3456")).isFalse();
        }
    }

    @Nested
    @DisplayName("count() method tests")
    class CountTests {

        @Test
        @DisplayName("Should return correct count of cards")
        void shouldReturnCorrectCount() {
            repository.save(activeCard);
            repository.save(inactiveCard);
            repository.save(expiringCard);

            assertThat(repository.count()).isEqualTo(3);
        }

        @Test
        @DisplayName("Should return zero when no cards")
        void shouldReturnZeroWhenNoCards() {
            assertThat(repository.count()).isZero();
        }

        @Test
        @DisplayName("Should update count after save and delete operations")
        void shouldUpdateCountAfterOperations() {
            assertThat(repository.count()).isZero();

            repository.save(activeCard);
            assertThat(repository.count()).isEqualTo(1);

            repository.save(inactiveCard);
            assertThat(repository.count()).isEqualTo(2);

            repository.deleteById("card-001");
            assertThat(repository.count()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("countActive() method tests")
    class CountActiveTests {

        @Test
        @DisplayName("Should return correct count of active cards")
        void shouldReturnCorrectCountOfActiveCards() {
            repository.save(activeCard);
            repository.save(inactiveCard);
            repository.save(expiringCard);
            repository.save(expiredCard);

            assertThat(repository.countActive()).isEqualTo(3);
        }

        @Test
        @DisplayName("Should return zero when no active cards")
        void shouldReturnZeroWhenNoActiveCards() {
            repository.save(inactiveCard);

            assertThat(repository.countActive()).isZero();
        }

        @Test
        @DisplayName("Should return zero when storage is empty")
        void shouldReturnZeroWhenStorageIsEmpty() {
            assertThat(repository.countActive()).isZero();
        }

        @Test
        @DisplayName("Should update count when card status changes")
        void shouldUpdateCountWhenCardStatusChanges() {
            repository.save(activeCard);
            assertThat(repository.countActive()).isEqualTo(1);

            activeCard.setActive(false);
            repository.save(activeCard);

            assertThat(repository.countActive()).isZero();
        }
    }

    @Nested
    @DisplayName("countExpiringCards() method tests")
    class CountExpiringCardsTests {

        @Test
        @DisplayName("Should return correct count of expiring cards")
        void shouldReturnCorrectCountOfExpiringCards() {
            repository.save(activeCard);
            repository.save(expiringCard);
            repository.save(expiredCard);

            assertThat(repository.countExpiringCards(2)).isEqualTo(0);
            assertThat(repository.countExpiringCards(60)).isEqualTo(1);
        }

        @Test
        @DisplayName("Should return zero when no cards expire within days")
        void shouldReturnZeroWhenNoCardsExpire() {
            repository.save(activeCard);

            assertThat(repository.countExpiringCards(30)).isZero();
        }

        @Test
        @DisplayName("Should handle zero days correctly")
        void shouldHandleZeroDays() {
            BankCard expiresToday = new BankCard(
                    "client-test",
                    "0000-0000-0000-0000",
                    LocalDate.now().minusYears(3),
                    LocalDate.now(),
                    "VISA"
            );
            expiresToday.setId("card-today");
            repository.save(expiresToday);
            repository.save(expiringCard);

            assertThat(repository.countExpiringCards(0)).isEqualTo(1);
        }

        @Test
        @DisplayName("Should not count expired cards")
        void shouldNotCountExpiredCards() {
            repository.save(expiredCard);
            repository.save(expiringCard);

            assertThat(repository.countExpiringCards(30)).isEqualTo(1);
        }
    }
}
