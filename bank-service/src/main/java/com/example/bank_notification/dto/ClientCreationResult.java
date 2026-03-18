package com.example.bank_notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientCreationResult {
    private ClientDto clientDto;
    private boolean isCreated;
}
