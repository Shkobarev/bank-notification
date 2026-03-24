package com.example.bank_notification.controller;


import com.example.bank_notification.dto.ClientDto;
import com.example.bank_notification.dto.request.CreateClientRequest;
import com.example.bank_notification.dto.request.UpdateEmailRequest;
import com.example.bank_notification.dto.ClientCreationResult;
import com.example.bank_notification.service.ClientService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(ClientController.class)
@DisplayName("ClientController Tests")
public class ClientControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClientService clientService;

    private final LocalDate birthDate = LocalDate.of(1990, 1, 15);
    private final String clientId = "550e8400-e29b-41d4-a716-446655440000";

    private ClientDto createTestClientDto() {
        ClientDto dto = new ClientDto();
        dto.setId(clientId);
        dto.setFullName("Иван Петров");
        dto.setBirthDate(birthDate);
        dto.setAge(34);
        dto.setEmail("ivan@mail.com");
        dto.setPhone("+79001234567");
        dto.setCardIds(List.of());
        return dto;
    }

    @Nested
    @DisplayName("POST /api/clients")
    class CreateClientTests {

        @Test
        @DisplayName("Should return 201 when new client created")
        void shouldReturn201WhenNewClientCreated() throws Exception {
            CreateClientRequest request = new CreateClientRequest();
            request.setFullName("Иван Петров");
            request.setBirthDate(birthDate);
            request.setEmail("ivan@mail.com");
            request.setPhone("+79001234567");
            request.setPassportNumber("1234567890");

            when(clientService.createClientWithResult(
                    anyString(), any(), anyString(), anyString(), anyString()))
                    .thenReturn(new ClientCreationResult(createTestClientDto(),true));

            mockMvc.perform(post("/api/clients")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(clientId))
                    .andExpect(jsonPath("$.fullName").value("Иван Петров"))
                    .andExpect(jsonPath("$.email").value("ivan@mail.com"));
        }

        @Test
        @DisplayName("Should return 200 when client already exists")
        void shouldReturn200WhenClientAlreadyExists() throws Exception {
            CreateClientRequest request = new CreateClientRequest();
            request.setFullName("Иван Петров");
            request.setBirthDate(birthDate);
            request.setEmail("ivan@mail.com");
            request.setPhone("+79001234567");
            request.setPassportNumber("1234567890");

            when(clientService.createClientWithResult(
                    anyString(), any(), anyString(), anyString(), anyString()))
                    .thenReturn(new ClientCreationResult(createTestClientDto(),false));

            mockMvc.perform(post("/api/clients")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(clientId));
        }

        @Test
        @DisplayName("Should return 400 when validation fails")
        void shouldReturn400WhenValidationFails() throws Exception {
            CreateClientRequest request = new CreateClientRequest();

            mockMvc.perform(post("/api/clients")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/clients/{id}")
    class GetClientByIdTests {

        @Test
        @DisplayName("Should return 200 when client exists")
        void shouldReturn200WhenClientExists() throws Exception {
            when(clientService.getClient(clientId)).thenReturn(Optional.of(createTestClientDto()));

            mockMvc.perform(get("/api/clients/{id}", clientId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(clientId))
                    .andExpect(jsonPath("$.fullName").value("Иван Петров"));
        }

        @Test
        @DisplayName("Should return 404 when client not found")
        void shouldReturn404WhenClientNotFound() throws Exception {
            when(clientService.getClient("not-found")).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/clients/{id}", "not-found"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/clients")
    class GetAllClientsTests {

        @Test
        @DisplayName("Should return 200 with list of clients")
        void shouldReturn200WithList() throws Exception {
            ClientDto client1 = createTestClientDto();
            ClientDto client2 = createTestClientDto();
            client2.setId("660f9500-f30c-52e5-b827-557766551111");
            client2.setFullName("Петр Иванов");

            when(clientService.getAllClients()).thenReturn(List.of(client1, client2));

            mockMvc.perform(get("/api/clients"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(clientId))
                    .andExpect(jsonPath("$[1].id").value("660f9500-f30c-52e5-b827-557766551111"))
                    .andExpect(jsonPath("$[0].fullName").value("Иван Петров"))
                    .andExpect(jsonPath("$[1].fullName").value("Петр Иванов"));
        }

        @Test
        @DisplayName("Should return 200 with empty list when no clients")
        void shouldReturn200WithEmptyList() throws Exception {
            when(clientService.getAllClients()).thenReturn(List.of());

            mockMvc.perform(get("/api/clients"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("PUT /api/clients/{id}/email")
    class UpdateEmailTests {

        @Test
        @DisplayName("Should return 200 when email updated")
        void shouldReturn200WhenEmailUpdated() throws Exception {
            String newEmail = "new@mail.com";
            UpdateEmailRequest request = new UpdateEmailRequest();
            request.setNewEmail(newEmail);

            ClientDto updatedClient = createTestClientDto();
            updatedClient.setEmail(newEmail);

            when(clientService.updateEmail(clientId, newEmail))
                    .thenReturn(Optional.of(updatedClient));

            mockMvc.perform(put("/api/clients/{id}/email", clientId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(newEmail));
        }

        @Test
        @DisplayName("Should return 404 when client not found")
        void shouldReturn404WhenClientNotFound() throws Exception {
            String newEmail = "new@mail.com";
            UpdateEmailRequest request = new UpdateEmailRequest();
            request.setNewEmail(newEmail);

            when(clientService.updateEmail("not-found", newEmail))
                    .thenReturn(Optional.empty());

            mockMvc.perform(put("/api/clients/{id}/email", "not-found")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/clients/{id}")
    class DeleteClientTests {

        @Test
        @DisplayName("Should return 204 when client deleted")
        void shouldReturn204WhenClientDeleted() throws Exception {
            when(clientService.deleteClient(clientId)).thenReturn(true);

            mockMvc.perform(delete("/api/clients/{id}", clientId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should return 404 when client not found")
        void shouldReturn404WhenClientNotFound() throws Exception {
            when(clientService.deleteClient("not-found")).thenReturn(false);

            mockMvc.perform(delete("/api/clients/{id}", "not-found"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/clients/search")
    class SearchByEmailTests {

        @Test
        @DisplayName("Should return 200 when client found by email")
        void shouldReturn200WhenClientFound() throws Exception {
            String email = "ivan@mail.com";
            when(clientService.findClientByEmail(email)).thenReturn(Optional.of(createTestClientDto()));

            mockMvc.perform(get("/api/clients/search")
                            .param("email", email))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(email));
        }

        @Test
        @DisplayName("Should return 404 when client not found by email")
        void shouldReturn404WhenClientNotFound() throws Exception {
            String email = "notfound@mail.com";
            when(clientService.findClientByEmail(email)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/clients/search")
                            .param("email", email))
                    .andExpect(status().isNotFound());
        }
    }
}
