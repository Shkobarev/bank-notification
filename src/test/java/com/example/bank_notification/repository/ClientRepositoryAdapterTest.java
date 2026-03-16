package com.example.bank_notification.repository;

import com.example.bank_notification.entity.ClientEntity;
import com.example.bank_notification.model.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientRepositoryAdapter Tests")
public class ClientRepositoryAdapterTest {
    @Mock
    private ClientJpaRepository jpaRepository;

    @InjectMocks
    private ClientRepositoryAdapter adapter;

    @Captor
    private ArgumentCaptor<ClientEntity> entityCaptor;

    private Client testClient;
    private ClientEntity testEntity;

    @BeforeEach
    void setUp() {
        testClient = new Client(
                "Иван Петров",
                LocalDate.of(1990, 1, 15),
                "ivan@mail.com",
                "1234 567890",
                "+79001234567"
        );
        testClient.setId("client-123");
        testClient.setCreatedAt(LocalDate.now());

        testEntity = new ClientEntity(
                "Иван Петров",
                LocalDate.of(1990, 1, 15),
                "ivan@mail.com",
                "1234 567890",
                "+79001234567"
        );
        testEntity.setId("client-123");
        testEntity.setCreatedAt(LocalDate.now());
    }

    @Nested
    @DisplayName("save() method tests")
    class SaveTests {

        @Test
        @DisplayName("Should save new client")
        void shouldSaveNewClient() {
            when(jpaRepository.save(any(ClientEntity.class))).thenAnswer(invocation -> {
                ClientEntity entity = invocation.getArgument(0);
                entity.setId("new-id-123");
                return entity;
            });

            Client saved = adapter.save(testClient);

            assertThat(saved).isNotNull();
            assertThat(saved.getId()).isEqualTo("new-id-123");
            assertThat(saved.getFullName()).isEqualTo("Иван Петров");
            assertThat(saved.getEmail()).isEqualTo("ivan@mail.com");

            verify(jpaRepository).save(entityCaptor.capture());
            ClientEntity captured = entityCaptor.getValue();
            assertThat(captured.getFullName()).isEqualTo("Иван Петров");
            assertThat(captured.getEmail()).isEqualTo("ivan@mail.com");
        }
    }

    @Nested
    @DisplayName("findById() method tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return client when found")
        void shouldReturnClientWhenFound() {
            when(jpaRepository.findById("client-123")).thenReturn(Optional.of(testEntity));

            Optional<Client> result = adapter.findById("client-123");

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo("client-123");
            assertThat(result.get().getFullName()).isEqualTo("Иван Петров");
        }

        @Test
        @DisplayName("Should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            when(jpaRepository.findById("not-found")).thenReturn(Optional.empty());

            Optional<Client> result = adapter.findById("not-found");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByEmail() method tests")
    class FindByEmailTests {

        @Test
        @DisplayName("Should return client when email found")
        void shouldReturnClientWhenEmailFound() {
            when(jpaRepository.findByEmail("ivan@mail.com")).thenReturn(Optional.of(testEntity));

            Optional<Client> result = adapter.findByEmail("ivan@mail.com");

            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("ivan@mail.com");
        }

        @Test
        @DisplayName("Should return empty when email not found")
        void shouldReturnEmptyWhenEmailNotFound() {
            when(jpaRepository.findByEmail("not@mail.com")).thenReturn(Optional.empty());

            Optional<Client> result = adapter.findByEmail("not@mail.com");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByFullNameAndBirthDate() method tests")
    class FindByFullNameAndBirthDateTests {

        @Test
        @DisplayName("Should return client when found")
        void shouldReturnClientWhenFound() {
            String fullName = "Иван Петров";
            LocalDate birthDate = LocalDate.of(1990, 1, 15);
            when(jpaRepository.findByFullNameAndBirthDate(fullName, birthDate))
                    .thenReturn(Optional.of(testEntity));

            Optional<Client> result = adapter.findByFullNameAndBirthDate(fullName, birthDate);

            assertThat(result).isPresent();
            assertThat(result.get().getFullName()).isEqualTo(fullName);
            assertThat(result.get().getBirthDate()).isEqualTo(birthDate);
        }

        @Test
        @DisplayName("Should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            String fullName = "Неизвестный";
            LocalDate birthDate = LocalDate.of(2000, 1, 1);
            when(jpaRepository.findByFullNameAndBirthDate(fullName, birthDate))
                    .thenReturn(Optional.empty());

            Optional<Client> result = adapter.findByFullNameAndBirthDate(fullName, birthDate);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("updateEmail() method tests")
    class UpdateEmailTests {

        @Test
        @DisplayName("Should update email successfully")
        void shouldUpdateEmailSuccessfully() {
            String clientId = "client-123";
            String newEmail = "new@mail.com";

            when(jpaRepository.findById(clientId)).thenReturn(Optional.of(testEntity));

            when(jpaRepository.save(any(ClientEntity.class))).thenAnswer(invocation -> {
                ClientEntity saved = invocation.getArgument(0);
                saved.setId(clientId);
                return saved;
            });

            Optional<Client> result = adapter.updateEmail(clientId, newEmail);

            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo(newEmail);

            verify(jpaRepository).findById(clientId);
            verify(jpaRepository).save(argThat(entity ->
                    entity.getEmail().equals(newEmail) &&
                            entity.getId().equals(clientId)
            ));
        }

        @Test
        @DisplayName("Should return empty when client not found")
        void shouldReturnEmptyWhenClientNotFound() {
            when(jpaRepository.findById("not-found")).thenReturn(Optional.empty());

            Optional<Client> result = adapter.updateEmail("not-found", "new@mail.com");

            assertThat(result).isEmpty();
            verify(jpaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("findAll() method tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return all clients")
        void shouldReturnAllClients() {
            List<ClientEntity> entities = List.of(testEntity);
            when(jpaRepository.findAll()).thenReturn(entities);

            List<Client> result = adapter.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo("client-123");
        }

        @Test
        @DisplayName("Should return empty list when no clients")
        void shouldReturnEmptyListWhenNoClients() {
            when(jpaRepository.findAll()).thenReturn(List.of());

            List<Client> result = adapter.findAll();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteById() method tests")
    class DeleteByIdTests {

        @Test
        @DisplayName("Should delete client when exists")
        void shouldDeleteClientWhenExists() {
            when(jpaRepository.existsById("client-123")).thenReturn(true);

            boolean result = adapter.deleteById("client-123");

            assertThat(result).isTrue();
            verify(jpaRepository).deleteById("client-123");
        }

        @Test
        @DisplayName("Should return false when client not exists")
        void shouldReturnFalseWhenClientNotExists() {
            when(jpaRepository.existsById("not-found")).thenReturn(false);

            boolean result = adapter.deleteById("not-found");

            assertThat(result).isFalse();
            verify(jpaRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("count() and existsById() tests")
    class CountAndExistsTests {

        @Test
        @DisplayName("Should return correct count")
        void shouldReturnCorrectCount() {
            when(jpaRepository.count()).thenReturn(5L);

            long result = adapter.count();

            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("Should return true when client exists")
        void shouldReturnTrueWhenClientExists() {
            when(jpaRepository.existsById("client-123")).thenReturn(true);

            boolean result = adapter.existsById("client-123");

            assertThat(result).isTrue();
        }
    }
}
