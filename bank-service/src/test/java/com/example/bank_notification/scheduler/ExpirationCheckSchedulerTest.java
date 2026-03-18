package com.example.bank_notification.scheduler;

import com.example.bank_notification.dto.CardDto;
import com.example.bank_notification.dto.NotificationDto;
import com.example.bank_notification.service.CardService;
import com.example.bank_notification.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExpirationCheckScheduler Tests")
public class ExpirationCheckSchedulerTest {
    @Mock
    private CardService cardService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ExpirationCheckScheduler scheduler;

    private CardDto testCard;
    private NotificationDto successNotification;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(scheduler, "schedulerEnabled", true);

        testCard = new CardDto();
        testCard.setId("card-123");
        testCard.setClientId("client-123");
        testCard.setDaysUntilExpired(14);

        successNotification = NotificationDto.builder()
                .success(true)
                .cardId("card-123")
                .build();
    }

    @Test
    @DisplayName("Should send notifications for cards with 30 days left")
    void shouldSendNotificationsFor30DaysCards() {
        when(cardService.getCardsExpiringExactly(30)).thenReturn(List.of(testCard));
        when(emailService.sendNotification(testCard)).thenReturn(successNotification);

        scheduler.checkCardsExpiringIn30Days();

        verify(cardService).getCardsExpiringExactly(30);
        verify(emailService).sendNotification(testCard);
    }

    @Test
    @DisplayName("Should not send notifications when scheduler is disabled")
    void shouldNotSendNotificationsWhenSchedulerDisabled() {
        ReflectionTestUtils.setField(scheduler, "schedulerEnabled", false);

        scheduler.checkCardsExpiringIn30Days();

        verify(cardService, never()).getCardsExpiringExactly(anyInt());
        verify(emailService, never()).sendNotification(any());
    }

    @Test
    @DisplayName("Should not send notifications when no cards found")
    void shouldNotSendNotificationsWhenNoCards() {
        when(cardService.getCardsExpiringExactly(14)).thenReturn(List.of());

        scheduler.checkCardsExpiringTest();

        verify(cardService).getCardsExpiringExactly(14);
        verify(emailService, never()).sendNotification(any());
    }

    @Test
    @DisplayName("Should handle error when getting cards")
    void shouldHandleErrorWhenGettingCards() {
        when(cardService.getCardsExpiringExactly(30)).thenThrow(new RuntimeException("Database error"));

        scheduler.checkCardsExpiringIn30Days();

        verify(cardService).getCardsExpiringExactly(30);
        verify(emailService, never()).sendNotification(any());
    }
}
