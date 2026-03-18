package com.example.bank_notification.service;

import com.example.bank_notification.dto.CardDto;
import com.example.bank_notification.dto.ClientDto;
import com.example.bank_notification.dto.NotificationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService Tests")
public class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    private CardDto testCard;
    private ClientDto testClient;

    @BeforeEach
    void setUp() {
        testCard = new CardDto();
        testCard.setId("card-123");
        testCard.setClientId("client-123");
        testCard.setCardNumber("1234-5678-9012-3456");
        testCard.setExpiryDate(LocalDate.now().plusDays(14));
        testCard.setDaysUntilExpired(14);

        testClient = new ClientDto();
        testClient.setId("client-123");
        testClient.setFullName("Иван Петров");
        testClient.setEmail("ivan@mail.com");
    }

    @Test
    @DisplayName("Should send email successfully")
    void shouldSendEmailSuccessfully() {
        when(clientService.getClient("client-123")).thenReturn(Optional.of(testClient));
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        NotificationDto result = emailService.sendNotification(testCard);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getErrorMessage()).isNull();

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertThat(sentMessage.getTo()).containsExactly("ivan@mail.com");
        assertThat(sentMessage.getSubject()).contains("14 дней");
        assertThat(sentMessage.getText()).contains("Иван Петров");
        assertThat(sentMessage.getText()).contains("****-****-****-3456");
    }

    @Test
    @DisplayName("Should return error when client has no email")
    void shouldReturnErrorWhenClientHasNoEmail() {
        testClient.setEmail(null);
        when(clientService.getClient("client-123")).thenReturn(Optional.of(testClient));

        NotificationDto result = emailService.sendNotification(testCard);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorMessage()).contains("У клиента нет email");
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should return error when client not found")
    void shouldReturnErrorWhenClientNotFound() {
        when(clientService.getClient("client-123")).thenReturn(Optional.empty());

        NotificationDto result = emailService.sendNotification(testCard);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorMessage()).contains("Клиент не найден");
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }
}
