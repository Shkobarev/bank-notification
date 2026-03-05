package com.example.bank_notification.repository;

import com.example.bank_notification.model.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ClientRepositoryImplTest {

    private ClientRepositoryImpl repository;

    private Client client1;
    private Client client2;

    @BeforeEach
    void setUp(){
        repository = new ClientRepositoryImpl();

        client1 = new Client(
                "Иван Петров",
                LocalDate.of(1990, 1, 15),
                "ivan@example.com",
                "1234 567890",
                "89001234567"
        );
        client1.setId("client-001");

        client2 = new Client(
                "Петр Иванов",
                LocalDate.of(1991, 1, 20),
                null,
                "1234 567891",
                null
        );
        client2.setId("client-002");
    }

    @Nested
    @DisplayName("save() method tests")
    class SaveTests {

        @Test
        @DisplayName("Should save client with all fields")
        void shouldSaveClientWithAllFields() {
            Client saved = repository.save(client1);

            assertThat(saved).isNotNull();
            assertThat(saved.getId()).isEqualTo("client-001");
            assertThat(saved.getFullName()).isEqualTo("Иван Петров");
            assertThat(saved.getEmail()).isEqualTo("ivan@example.com");
            assertThat(saved.getPhone()).isEqualTo("89001234567");
            assertThat(saved.getPassportNumber()).isEqualTo("1234 567890");
            assertThat(saved.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 15));
        }

        @Test
        @DisplayName("Should save client without email and phone")
        void shouldSaveClientWithoutEmailAndPhone() {
            Client saved = repository.save(client2);

            assertThat(saved).isNotNull();
            assertThat(saved.getId()).isEqualTo("client-002");
            assertThat(saved.getEmail()).isNull();
            assertThat(saved.getPhone()).isNull();
        }

        @Test
        @DisplayName("Should update existing client")
        void shouldUpdateExistingClient() {
            repository.save(client1);
            client1.setFullName("Иван Петров Обновленный");

            Client updated = repository.save(client1);

            assertThat(updated.getFullName()).isEqualTo("Иван Петров Обновленный");
            Optional<Client> found = repository.findById("client-001");
            assertThat(found).isPresent();
            assertThat(found.get().getFullName()).isEqualTo("Иван Петров Обновленный");
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
        @DisplayName("Should find client by existing ID")
        void shouldFindClientByExistingId() {
            repository.save(client1);

            Optional<Client> found = repository.findById("client-001");

            assertThat(found).isPresent();
            assertThat(found.get().getFullName()).isEqualTo("Иван Петров");
        }

        @Test
        @DisplayName("Should return empty Optional when ID not found")
        void shouldReturnEmptyWhenIdNotFound() {
            Optional<Client> found = repository.findById("non-existent-id");

            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should return empty Optional when ID is null")
        void shouldReturnEmptyWhenIdIsNull() {
            Optional<Client> found = repository.findById(null);

            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByEmail() method tests")
    class FindByEmailTests {

        @Test
        @DisplayName("Should find client by existing email")
        void shouldFindClientByExistingEmail() {
            repository.save(client1);

            Optional<Client> found = repository.findByEmail("ivan@example.com");

            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo("client-001");
        }

        @Test
        @DisplayName("Should return empty Optional when email not found")
        void shouldReturnEmptyWhenEmailNotFound() {
            repository.save(client1);

            Optional<Client> found = repository.findByEmail("nonexistent@example.com");

            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should return empty Optional when email is null")
        void shouldReturnEmptyWhenEmailIsNull() {
            Optional<Client> found = repository.findByEmail(null);

            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should not find client with null email")
        void shouldNotFindClientWithNullEmail() {
            repository.save(client2);

            Optional<Client> found = repository.findByEmail(null);

            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByFullNameAndBirthDate() method tests")
    class FindByFullNameAndBirthDateTests {

        @Test
        @DisplayName("Should find client by existing full name and birth date")
        void shouldFindClientByExistingFullNameAndBirthDate() {
            repository.save(client1);

            Optional<Client> found = repository.findByFullNameAndBirthDate(
                    "Иван Петров", LocalDate.of(1990, 1, 15));

            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo("client-001");
        }

        @Test
        @DisplayName("Should return empty Optional when combination not found")
        void shouldReturnEmptyWhenCombinationNotFound() {
            repository.save(client1);

            Optional<Client> found = repository.findByFullNameAndBirthDate(
                    "Иван Сидоров", LocalDate.of(1991, 1, 15));

            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should return empty Optional when full name is null")
        void shouldReturnEmptyWhenFullNameIsNull() {
            Optional<Client> found = repository.findByFullNameAndBirthDate(
                    null, LocalDate.of(1990, 1, 15));

            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should return empty Optional when birth date is null")
        void shouldReturnEmptyWhenBirthDateIsNull() {
            Optional<Client> found = repository.findByFullNameAndBirthDate(
                    "Иван Петров", null);

            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should return empty Optional when both parameters are null")
        void shouldReturnEmptyWhenBothParametersAreNull() {
            Optional<Client> found = repository.findByFullNameAndBirthDate(null, null);

            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("updateEmail() method tests")
    class UpdateEmailTests {

        @Test
        @DisplayName("Should update email for existing client")
        void shouldUpdateEmailForExistingClient() {
            repository.save(client1);

            Optional<Client> updated = repository.updateEmail("client-001", "newemail@example.com");

            assertThat(updated).isPresent();
            assertThat(updated.get().getEmail()).isEqualTo("newemail@example.com");
            assertThat(repository.findByEmail("ivan@example.com")).isEmpty();
            assertThat(repository.findByEmail("newemail@example.com")).isPresent();
        }

        @Test
        @DisplayName("Should set email for client who had no email")
        void shouldSetEmailForClientWithNoEmail() {
            repository.save(client2);

            Optional<Client> updated = repository.updateEmail("client-002", "newemail@example.com");

            assertThat(updated).isPresent();
            assertThat(updated.get().getEmail()).isEqualTo("newemail@example.com");
            assertThat(repository.findByEmail("newemail@example.com")).isPresent();
        }

        @Test
        @DisplayName("Should remove email from client")
        void shouldRemoveEmailFromClient() {
            repository.save(client1);

            Optional<Client> updated = repository.updateEmail("client-001", null);

            assertThat(updated).isPresent();
            assertThat(updated.get().getEmail()).isNull();
            assertThat(repository.findByEmail("ivan@example.com")).isEmpty();
        }

        @Test
        @DisplayName("Should return empty Optional when client not found")
        void shouldReturnEmptyWhenClientNotFound() {
            Optional<Client> updated = repository.updateEmail("non-existent", "email@example.com");

            assertThat(updated).isEmpty();
        }

        @Test
        @DisplayName("Should return empty Optional when clientId is null")
        void shouldReturnEmptyWhenClientIdIsNull() {
            Optional<Client> updated = repository.updateEmail(null, "email@example.com");

            assertThat(updated).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAll() method tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return all clients")
        void shouldReturnAllClients() {
            repository.save(client1);
            repository.save(client2);

            List<Client> allClients = repository.findAll();

            assertThat(allClients).hasSize(2);
            assertThat(allClients).extracting(Client::getId)
                    .containsExactlyInAnyOrder("client-001", "client-002");
        }

        @Test
        @DisplayName("Should return empty list when no clients exist")
        void shouldReturnEmptyListWhenNoClients() {
            List<Client> allClients = repository.findAll();

            assertThat(allClients).isEmpty();
        }

        @Test
        @DisplayName("Should return a copy of data, not the original reference")
        void shouldReturnCopyOfData() {
            repository.save(client1);
            List<Client> allClients = repository.findAll();

            allClients.clear();

            assertThat(repository.findAll()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("deleteById() method tests")
    class DeleteByIdTests {

        @Test
        @DisplayName("Should delete client by ID")
        void shouldDeleteClientById() {
            repository.save(client1);

            boolean deleted = repository.deleteById("client-001");

            assertThat(deleted).isTrue();
            assertThat(repository.findById("client-001")).isEmpty();
            assertThat(repository.count()).isZero();
        }

        @Test
        @DisplayName("Should remove all indexes when deleting client")
        void shouldRemoveAllIndexesWhenDeletingClient() {
            repository.save(client1);

            repository.deleteById("client-001");

            assertThat(repository.findByEmail("ivan@example.com")).isEmpty();
            assertThat(repository.findByFullNameAndBirthDate(
                    "Иван Петров", LocalDate.of(1990, 1, 15))).isEmpty();
        }

        @Test
        @DisplayName("Should return false when deleting non-existent client")
        void shouldReturnFalseWhenDeletingNonExistentClient() {
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
        @DisplayName("Should correctly delete client without email and phone")
        void shouldDeleteClientWithoutEmailAndPhone() {
            repository.save(client2);

            boolean deleted = repository.deleteById("client-002");

            assertThat(deleted).isTrue();
            assertThat(repository.findById("client-002")).isEmpty();
        }
    }

    @Nested
    @DisplayName("count() method tests")
    class CountTests {

        @Test
        @DisplayName("Should return correct count of clients")
        void shouldReturnCorrectCount() {
            repository.save(client1);
            repository.save(client2);

            assertThat(repository.count()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should return zero when no clients")
        void shouldReturnZeroWhenNoClients() {
            assertThat(repository.count()).isZero();
        }

        @Test
        @DisplayName("Should update count after save and delete operations")
        void shouldUpdateCountAfterOperations() {
            assertThat(repository.count()).isZero();

            repository.save(client1);
            assertThat(repository.count()).isEqualTo(1);

            repository.save(client2);
            assertThat(repository.count()).isEqualTo(2);

            repository.deleteById("client-001");
            assertThat(repository.count()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("existsById() method tests")
    class ExistsByIdTests {

        @Test
        @DisplayName("Should return true for existing client ID")
        void shouldReturnTrueForExistingId() {
            repository.save(client1);

            assertThat(repository.existsById("client-001")).isTrue();
        }

        @Test
        @DisplayName("Should return false for non-existent client ID")
        void shouldReturnFalseForNonExistentId() {
            assertThat(repository.existsById("non-existent")).isFalse();
        }

        @Test
        @DisplayName("Should return false when ID is null")
        void shouldReturnFalseWhenIdIsNull() {
            assertThat(repository.existsById(null)).isFalse();
        }

        @Test
        @DisplayName("Should return false for deleted client")
        void shouldReturnFalseForDeletedClient() {
            repository.save(client1);
            repository.deleteById("client-001");

            assertThat(repository.existsById("client-001")).isFalse();
        }
    }
}
