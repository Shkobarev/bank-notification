package com.example.bank_notification.scheduler;

import com.example.bank_notification.dto.CardDto;
import com.example.bank_notification.dto.NotificationDto;
import com.example.bank_notification.service.CardService;
import com.example.bank_notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ExpirationCheckScheduler {
    private final CardService cardService;
    private final EmailService emailService;

    @Value("${scheduler.enabled:true}")
    private boolean schedulerEnabled;

    @Scheduled(cron = "${scheduler.cron.daily}")
    public void checkCardsExpiringIn30Days() {
        if (!schedulerEnabled) return;
        processCards(30,"30 дней");
    }

//    @Scheduled(cron = "${scheduler.cron.test}")
//    public void checkCardsExpiringTest() {
//        if (!schedulerEnabled) return;
//        processCards(14, "14 дней");
//    }

    private void processCards(int days, String period) {
        try {
            List<CardDto> cards = cardService.getCardsExpiringExactly(days);

            if (cards.isEmpty()) {
                log.info("Нет карт, истекающих через {}", period);
                return;
            }

            for (CardDto card : cards) {
                NotificationDto result = emailService.sendNotification(card);
                if (!result.isSuccess())
                    log.warn("Не удалось отправить уведомление для карты {}: {}",
                            card.getId(), result.getErrorMessage());
            }

        } catch (Exception e) {
            log.error("Ошибка при обработке карт: {}", e.getMessage(), e);
        }
    }
}
