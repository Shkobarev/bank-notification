package com.example.bank_notification.service;

import com.example.bank_notification.dto.CardDto;
import com.example.bank_notification.dto.ClientDto;
import com.example.bank_notification.dto.NotificationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final ClientService clientService;
    private final String fromEmail;

    public EmailService(JavaMailSender mailSender, ClientService clientService,
                        @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.clientService = clientService;
        this.fromEmail = fromEmail;
    }

    public NotificationDto sendNotification(CardDto card) {
        NotificationDto.NotificationDtoBuilder builder = NotificationDto.builder()
                .cardId(card.getId())
                .maskedCardNumber(card.getMaskedNumber())
                .expiryDate(card.getExpiryDate())
                .daysUntilExpired(card.getDaysUntilExpired())
                .notificationType("EMAIL")
                .sentAt(LocalDateTime.now());

        try {
            ClientDto client = clientService.getClient(card.getClientId())
                    .orElseThrow(() -> new RuntimeException("Клиент не найден: " + card.getClientId()));

            builder.clientId(client.getId())
                    .clientName(client.getFullName())
                    .clientEmail(client.getEmail());

            if (client.getEmail() == null || client.getEmail().isBlank()) {
                builder.success(false).errorMessage("У клиента нет email");
                return builder.build();
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(client.getEmail());
            message.setSubject("Срок действия карты истекает через " + card.getDaysUntilExpired() + " дней");
            message.setText(buildEmailText(client, card));

            mailSender.send(message);

            builder.success(true);
            log.info("Письмо отправлено {}", client.getEmail());

        } catch (Exception e) {
            builder.success(false).errorMessage(e.getMessage());
            log.error("Ошибка отправки письма: {}", e.getMessage());
        }

        return builder.build();
    }

    private String buildEmailText(ClientDto client, CardDto card) {
        return String.format(
                "Уважаемый(ая) %s!\n\n" +
                "Срок действия вашей карты истекает через %d дней.\n\n" +
                "Детали карты:\n" +
                "• Номер: %s\n" +
                "• Дата истечения: %s\n" +
                "• Осталось дней: %d\n\n" +
                "Пожалуйста, позаботьтесь о перевыпуске карты заранее.\n\n" +
                "С уважением,\n" +
                "Разработчик😎\n" +
                "%s",
                client.getFullName(),
                card.getDaysUntilExpired(),
                card.getMaskedNumber(),
                card.getExpiryDate(),
                card.getDaysUntilExpired(),
                LocalDateTime.now().getYear()
        );
    }
}
