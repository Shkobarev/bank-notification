package com.example.bank_notification;

import com.example.bank_notification.dto.NotificationDto;
import com.example.bank_notification.model.BankCard;
import com.example.bank_notification.model.Client;
import com.example.bank_notification.repository.CardRepository;
import com.example.bank_notification.repository.ClientRepository;
import com.example.bank_notification.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class BankNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankNotificationApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner testData(ClientRepository clientRepository, CardRepository cardRepository) {
//		return args -> {
//			try {
//				Client client = new Client(
//						"Петр Петров",
//						LocalDate.of(2004, 7, 3),
//						"",
//						"1234 567890",
//						""
//				);
//
//				client = clientRepository.save(client);
//				log.info("Создан клиент:");
//				log.info("   ID: {}", client.getId());
//				log.info("   Имя: {}", client.getFullName());
//				log.info("   Email: {}", client.getEmail());
//
//				BankCard card14;
//                card14 = new BankCard(
//                        client.getId(),
//                        "4111111111111111",
//                        LocalDate.now(),
//                        LocalDate.now().plusDays(14),
//                        "VISA"
//                );
//
//                card14 = cardRepository.save(card14);
//				log.info("Создана карта (14 дней):");
//				log.info("   ID: {}", card14.getId());
//				log.info("   Номер: {}", card14.getCardNumber());
//				log.info("   Истекает: {}", card14.getExpiryDate());
//				log.info("   Осталось дней: {}", card14.daysUntilExpired());
//
//			} catch (Exception e) {
//				log.error("Ошибка при создании тестовых данных: {}", e.getMessage(), e);
//			}
//		};
//	}
}
