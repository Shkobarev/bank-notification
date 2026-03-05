package com.example.bank_notification.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateCardRequest {
    @NotBlank(message = "Card type is required")
    @Pattern(regexp = "VISA|Mastercard|MIR",
            message = "Card type must be one of: VISA, Mastercard, MIR")
    private String cardType;

    @Min(value = 1, message = "Card validity must be at least 1 year")
    @Max(value = 5, message = "Card validity cannot exceed 5 years")
    private Integer validityYears = 3;
}
