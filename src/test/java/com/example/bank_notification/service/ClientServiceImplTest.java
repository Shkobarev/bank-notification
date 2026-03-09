package com.example.bank_notification.service;

import com.example.bank_notification.dto.CardDto;
import com.example.bank_notification.dto.ClientDto;
import com.example.bank_notification.dto.mapper.ClientMapper;
import com.example.bank_notification.model.Client;
import com.example.bank_notification.repository.ClientRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientServiceImpl Tests")
public class ClientServiceImplTest {
    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CardService cardService;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client testClient;
    private ClientDto testClientDto;
    private CardDto testCardDto;

    @BeforeEach
    void setUp() {
        testClient = new Client(
                "Иван Петров",
                LocalDate.of(1990, 1, 15),
                "ivan@mail.com",
                "1234 567890",
                "89001234567"
        );
        testClient.setId("client-123");

        testClientDto = new ClientDto();
        testClientDto.setId("client-123");
        testClientDto.setFullName("Иван Петров");
        testClientDto.setEmail("ivan@mail.com");

        testCardDto = new CardDto();
        testCardDto.setId("card-456");
    }

    @Nested
    @DisplayName("createClient() tests")
    class CreateClientTests {

        @Test
        @DisplayName("Should create new client when not exists")
        void shouldCreateNewClientWhenNotExists() {
            String fullName = "Иван Петров";
            LocalDate birthDate = LocalDate.of(1990, 1, 15);
            String email = "ivan@mail.com";
            String phone = "89001234567";
            String passport = "1234 567890";

            when(clientRepository.findByFullNameAndBirthDate(fullName, birthDate))
                    .thenReturn(Optional.empty());
            when(clientRepository.findByEmail(email)).thenReturn(Optional.empty());
            when(clientRepository.save(any(Client.class))).thenReturn(testClient);
            when(clientMapper.toDto(testClient)).thenReturn(testClientDto);

            ClientDto result = clientService.createClient(fullName, birthDate, email, phone, passport);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("client-123");

            verify(clientRepository).findByFullNameAndBirthDate(fullName, birthDate);
            verify(clientRepository).findByEmail(email);
            verify(clientRepository).save(any(Client.class));
            verify(clientMapper).toDto(testClient);
        }

        @Test
        @DisplayName("Should return existing client when found by fullName and birthDate")
        void shouldReturnExistingClientWhenFound() {
            String fullName = "Иван Петров";
            LocalDate birthDate = LocalDate.of(1990, 1, 15);

            when(clientRepository.findByFullNameAndBirthDate(fullName, birthDate))
                    .thenReturn(Optional.of(testClient));
            when(clientMapper.toDto(testClient)).thenReturn(testClientDto);
            when(cardService.getClientCards("client-123")).thenReturn(List.of(testCardDto));

            ClientDto result = clientService.createClient(fullName, birthDate,
                    "new@mail.com", "89999999999", "9876 543210");

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("client-123");
            assertThat(result.getEmail()).isEqualTo("ivan@mail.com"); // старый email!

            verify(clientRepository, never()).save(any(Client.class));
            verify(cardService).getClientCards("client-123");
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailExists() {
            String email = "existing@mail.com";

            when(clientRepository.findByFullNameAndBirthDate(any(), any()))
                    .thenReturn(Optional.empty());
            when(clientRepository.findByEmail(email)).thenReturn(Optional.of(testClient));

            assertThatThrownBy(() ->
                    clientService.createClient("Петр Иванов", LocalDate.of(1985, 5, 20),
                            email, "89001234567", "1234 567890")
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email already in use");
        }
    }

    @Nested
    @DisplayName("getClient() tests")
    class GetClientTests {

        @Test
        @DisplayName("Should return client when found")
        void shouldReturnClientWhenFound() {
            String clientId = "client-123";

            when(clientRepository.findById(clientId)).thenReturn(Optional.of(testClient));
            when(clientMapper.toDto(testClient)).thenReturn(testClientDto);
            when(cardService.getClientCards(clientId)).thenReturn(List.of(testCardDto));

            Optional<ClientDto> result = clientService.getClient(clientId);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo("client-123");
        }

        @Test
        @DisplayName("Should return empty when client not found")
        void shouldReturnEmptyWhenClientNotFound() {
            String clientId = "non-existent";
            when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

            Optional<ClientDto> result = clientService.getClient(clientId);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getAllClients() tests")
    class GetAllClientsTests {

        @Test
        @DisplayName("Should return all clients")
        void shouldReturnAllClients() {
            List<Client> clients = List.of(testClient);

            when(clientRepository.findAll()).thenReturn(clients);
            when(clientMapper.toDtoList(clients)).thenReturn(List.of(testClientDto));
            when(cardService.getClientCards("client-123")).thenReturn(List.of(testCardDto));

            List<ClientDto> result = clientService.getAllClients();

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo("client-123");
        }

        @Test
        @DisplayName("Should return empty list when no clients")
        void shouldReturnEmptyListWhenNoClients() {
            when(clientRepository.findAll()).thenReturn(List.of());
            when(clientMapper.toDtoList(List.of())).thenReturn(List.of());

            List<ClientDto> result = clientService.getAllClients();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("updateEmail() tests")
    class UpdateEmailTests {

        @Test
        @DisplayName("Should update email successfully")
        void shouldUpdateEmailSuccessfully() {
            String clientId = "client-123";
            String newEmail = "new@mail.com";

            when(clientRepository.findById(clientId)).thenReturn(Optional.of(testClient));
            when(clientRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
            when(clientRepository.updateEmail(clientId, newEmail)).thenReturn(Optional.of(testClient));
            when(clientMapper.toDto(testClient)).thenReturn(testClientDto);
            when(cardService.getClientCards(clientId)).thenReturn(List.of(testCardDto));

            Optional<ClientDto> result = clientService.updateEmail(clientId, newEmail);

            assertThat(result).isPresent();
            verify(clientRepository).updateEmail(clientId, newEmail);
        }

        @Test
        @DisplayName("Should throw exception when email already used by another client")
        void shouldThrowExceptionWhenEmailAlreadyUsed() {
            String clientId = "client-123";
            String newEmail = "other@mail.com";
            Client otherClient = new Client();
            otherClient.setId("client-456");

            when(clientRepository.findById(clientId)).thenReturn(Optional.of(testClient));
            when(clientRepository.findByEmail(newEmail)).thenReturn(Optional.of(otherClient));

            assertThatThrownBy(() -> clientService.updateEmail(clientId, newEmail))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email already in use");
        }

        @Test
        @DisplayName("Should throw exception when client not found")
        void shouldThrowExceptionWhenClientNotFound() {
            String clientId = "non-existent";
            when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clientService.updateEmail(clientId, "new@mail.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Client not found");
        }
    }

    @Nested
    @DisplayName("deleteClient() tests")
    class DeleteClientTests {

        @Test
        @DisplayName("Should delete client when no active cards")
        void shouldDeleteClientWhenNoActiveCards() {
            String clientId = "client-123";

            when(cardService.getActiveClientCards(clientId)).thenReturn(List.of());
            when(clientRepository.deleteById(clientId)).thenReturn(true);

            boolean result = clientService.deleteClient(clientId);

            assertThat(result).isTrue();
            verify(clientRepository).deleteById(clientId);
        }

        @Test
        @DisplayName("Should throw exception when client has active cards")
        void shouldThrowExceptionWhenClientHasActiveCards() {
            String clientId = "client-123";

            when(cardService.getActiveClientCards(clientId)).thenReturn(List.of(testCardDto));

            assertThatThrownBy(() -> clientService.deleteClient(clientId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot delete client with active cards");

            verify(clientRepository, never()).deleteById(anyString());
        }
    }

    @Nested
    @DisplayName("clientExists() tests")
    class ClientExistsTests {

        @Test
        @DisplayName("Should return true when client exists")
        void shouldReturnTrueWhenClientExists() {
            String clientId = "client-123";
            when(clientRepository.existsById(clientId)).thenReturn(true);

            boolean result = clientService.clientExists(clientId);

            assertThat(result).isTrue();
            verify(clientRepository).existsById(clientId);
        }

        @Test
        @DisplayName("Should return false when client does not exist")
        void shouldReturnFalseWhenClientDoesNotExist() {
            String clientId = "non-existent";
            when(clientRepository.existsById(clientId)).thenReturn(false);

            boolean result = clientService.clientExists(clientId);

            assertThat(result).isFalse();
            verify(clientRepository).existsById(clientId);
        }
    }

    @Nested
    @DisplayName("findClientByEmail() tests")
    class FindClientByEmailTests {

        @Test
        @DisplayName("Should return client when email exists")
        void shouldReturnClientWhenEmailExists() {
            String email = "ivan@mail.com";

            when(clientRepository.findByEmail(email)).thenReturn(Optional.of(testClient));
            when(clientMapper.toDto(testClient)).thenReturn(testClientDto);
            when(cardService.getClientCards("client-123")).thenReturn(List.of(testCardDto));

            Optional<ClientDto> result = clientService.findClientByEmail(email);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo("client-123");
            assertThat(result.get().getCardIds()).contains("card-456");

            verify(clientRepository).findByEmail(email);
            verify(cardService).getClientCards("client-123");
        }

        @Test
        @DisplayName("Should return empty when email not found")
        void shouldReturnEmptyWhenEmailNotFound() {
            String email = "nonexistent@mail.com";
            when(clientRepository.findByEmail(email)).thenReturn(Optional.empty());

            Optional<ClientDto> result = clientService.findClientByEmail(email);

            assertThat(result).isEmpty();
            verify(clientRepository).findByEmail(email);
            verifyNoInteractions(cardService);
        }
    }

    @Nested
    @DisplayName("findClientByFullNameAndBirthDate() tests")
    class FindClientByFullNameAndBirthDateTests {

        private final String fullName = "Иван Петров";
        private final LocalDate birthDate = LocalDate.of(1990, 1, 15);

        @Test
        @DisplayName("Should return client when combination exists")
        void shouldReturnClientWhenCombinationExists() {
            when(clientRepository.findByFullNameAndBirthDate(fullName, birthDate))
                    .thenReturn(Optional.of(testClient));
            when(clientMapper.toDto(testClient)).thenReturn(testClientDto);
            when(cardService.getClientCards("client-123")).thenReturn(List.of(testCardDto));

            Optional<ClientDto> result = clientService.findClientByFullNameAndBirthDate(fullName, birthDate);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo("client-123");
            assertThat(result.get().getCardIds()).contains("card-456");

            verify(clientRepository).findByFullNameAndBirthDate(fullName, birthDate);
            verify(cardService).getClientCards("client-123");
        }

        @Test
        @DisplayName("Should return empty when combination not found")
        void shouldReturnEmptyWhenCombinationNotFound() {
            when(clientRepository.findByFullNameAndBirthDate(fullName, birthDate))
                    .thenReturn(Optional.empty());

            Optional<ClientDto> result = clientService.findClientByFullNameAndBirthDate(fullName, birthDate);

            assertThat(result).isEmpty();
            verify(clientRepository).findByFullNameAndBirthDate(fullName, birthDate);
            verifyNoInteractions(cardService);
        }
    }
}
