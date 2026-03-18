package com.example.bank_notification.service.mapper;

import com.example.bank_notification.dto.ClientDto;
import com.example.bank_notification.model.Client;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ClientMapper {
    /**
     * Преобразует Client в ClientDto.
     * Вычисляет age на основе birthDate.
     */
    public ClientDto toDto(Client client) {
        if (client == null) {
            return null;
        }

        ClientDto dto = new ClientDto();
        dto.setId(client.getId());
        dto.setFullName(client.getFullName());
        dto.setBirthDate(client.getBirthDate());
        dto.setAge(calculateAge(client.getBirthDate()));
        dto.setEmail(client.getEmail());
        dto.setPhone(client.getPhone());
        dto.setPassportNumber(client.getPassportNumber());
        dto.setCreatedAt(client.getCreatedAt());

        return dto;
    }

    /**
     * Преобразует список Client в список ClientDto.
     */
    public List<ClientDto> toDtoList(List<Client> clients) {
        return clients.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Вычисляет возраст клиента на основе даты рождения.
     */
    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        } else {
            return Period.between(birthDate, LocalDate.now()).getYears();
        }
    }
}
