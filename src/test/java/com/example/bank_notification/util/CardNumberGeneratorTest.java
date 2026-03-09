package com.example.bank_notification.util;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CardNumberGenerator Tests")
public class CardNumberGeneratorTest {
    private CardNumberGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new CardNumberGenerator();
    }

    @Nested
    @DisplayName("generate() method tests")
    class GenerateTests {

        @Test
        @DisplayName("Should generate VISA card number starting with 4")
        void shouldGenerateVISANumber() {
            String cardNumber = generator.generate("VISA");

            assertThat(cardNumber).matches("\\d{4}-\\d{4}-\\d{4}-\\d{4}");
            assertThat(cardNumber).startsWith("4");
            assertThat(generator.isValid(cardNumber)).isTrue();
        }

        @Test
        @DisplayName("Should generate Mastercard card number starting with 5")
        void shouldGenerateMastercardNumber() {
            String cardNumber = generator.generate("Mastercard");

            assertThat(cardNumber).matches("\\d{4}-\\d{4}-\\d{4}-\\d{4}");
            assertThat(cardNumber).startsWith("5");
            assertThat(generator.isValid(cardNumber)).isTrue();
        }

        @Test
        @DisplayName("Should generate MIR card number starting with 2")
        void shouldGenerateMIRNumber() {
            String cardNumber = generator.generate("MIR");

            assertThat(cardNumber).matches("\\d{4}-\\d{4}-\\d{4}-\\d{4}");
            assertThat(cardNumber).startsWith("2");
            assertThat(generator.isValid(cardNumber)).isTrue();
        }

        @Test
        @DisplayName("Should throw exception for unsupported card type")
        void shouldThrowExceptionForUnsupportedType() {
            assertThatThrownBy(() -> generator.generate("AmericanExpress"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unsupported card type: AmericanExpress");
        }

        @RepeatedTest(10)
        @DisplayName("Generated card number should always be valid")
        void generatedCardNumberShouldAlwaysBeValid() {
            String visaNumber = generator.generate("VISA");
            String mastercardNumber = generator.generate("Mastercard");
            String mirNumber = generator.generate("MIR");

            assertThat(generator.isValid(visaNumber)).isTrue();
            assertThat(generator.isValid(mastercardNumber)).isTrue();
            assertThat(generator.isValid(mirNumber)).isTrue();
        }

        @Test
        @DisplayName("Generated numbers should be unique")
        void generatedNumbersShouldBeUnique() {
            String number1 = generator.generate("VISA");
            String number2 = generator.generate("VISA");
            String number3 = generator.generate("VISA");

            assertThat(number1).isNotEqualTo(number2);
            assertThat(number1).isNotEqualTo(number3);
            assertThat(number2).isNotEqualTo(number3);
        }
    }

    @Nested
    @DisplayName("isValid() method tests")
    class IsValidTests {

        @ParameterizedTest
        @CsvSource({
                "4111-1111-1111-1111, true",
                "5555-5555-5555-4444, true",
                "5123-4567-8901-2346, true",
                "1234-5678-9012-3456, false",
                "4532-1234-5678-9012, false"
        })
        @DisplayName("Should correctly validate card numbers")
        void shouldCorrectlyValidateCardNumbers(String cardNumber, boolean expected) {
            assertThat(generator.isValid(cardNumber)).isEqualTo(expected);
        }

        @Test
        @DisplayName("Should return false for null")
        void shouldReturnFalseForNull() {
            assertThat(generator.isValid(null)).isFalse();
        }

        @Test
        @DisplayName("Should return false for empty string")
        void shouldReturnFalseForEmptyString() {
            assertThat(generator.isValid("")).isFalse();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "4111-1111-1111-111",
                "4111-1111-1111-11111",
                "4111-1111-1111-111a",
                "4111-1111-1111-1111-1111"
        })
        @DisplayName("Should return false for invalid format")
        void shouldReturnFalseForInvalidFormat(String cardNumber) {
            assertThat(generator.isValid(cardNumber)).isFalse();
        }
    }
}
