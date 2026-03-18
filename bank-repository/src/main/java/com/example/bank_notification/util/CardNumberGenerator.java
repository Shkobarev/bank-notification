package com.example.bank_notification.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

/**
 * Генератор уникальных номеров банковских карт.
 */
@Component
public class CardNumberGenerator {
    private static final Map<String, String> CARD_PREFIXES = Map.of(
            "VISA", "4",
            "Mastercard", "5",
            "MIR", "2"
    );

    private static final int CARD_NUMBER_LENGTH = 16;
    private final Random random = new SecureRandom();

    /**
     * Генерирует номер карты на основе типа.
     *
     * @param cardType тип карты (VISA, Mastercard, MIR)
     * @return номер карты в формате XXXX-XXXX-XXXX-XXXX
     */
    public String generate(String cardType) {
        String prefix = CARD_PREFIXES.get(cardType);
        if (prefix == null) {
            throw new IllegalArgumentException("Unsupported card type: " + cardType);
        }

        StringBuilder number = new StringBuilder(prefix);

        for (int i = prefix.length(); i < CARD_NUMBER_LENGTH - 1; i++) {
            number.append(random.nextInt(10));
        }

        int checkDigit = calculateLunCheckDigit(number.toString());
        number.append(checkDigit);

        return formatCardNumber(number.toString());
    }

    /**
     * Вычисление контрольной суммы по алгоритму Луна.
     */
    private int calculateLunCheckDigit(String number) {
        int sum = 0;
        boolean doubleDigit = true;
        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));

            if (doubleDigit) {
                digit = (digit * 2) - (digit > 4 ? 9 : 0);
            }

            sum += digit;
            doubleDigit = !doubleDigit;
        }
        return (sum * 9) % 10;
    }

    /**
     * Форматирует номер карты с разделителями.
     */
    private String formatCardNumber(String number) {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < number.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append('-');
            }
            formatted.append(number.charAt(i));
        }
        return formatted.toString();
    }

    /**
     * Проверяет валидность номера карты по алгоритму Луна.
     *
     * @param cardNumber номер карты
     * @return true если номер карты валидный, иначе false
     */
    public boolean isValid(String cardNumber) {
        if(cardNumber == null) return false;
        String cleanNumber = cardNumber.replaceAll("[^0-9]", "");
        if (cleanNumber.length() != CARD_NUMBER_LENGTH) return false;

        int sum = 0;
        boolean doubleDigit = false;
        for (int i = cleanNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cleanNumber.charAt(i));

            if (doubleDigit) {
                digit = (digit * 2) - (digit > 4 ? 9 : 0);
            }

            sum += digit;
            doubleDigit = !doubleDigit;
        }
        return sum % 10 == 0;
    }
}
