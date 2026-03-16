package com.example.bank_notification.service;

import com.example.bank_notification.dto.CardDto;
import com.example.bank_notification.dto.ClientDto;
import com.example.bank_notification.dto.mapper.ClientMapper;
import com.example.bank_notification.model.Client;
import com.example.bank_notification.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService{

    private final ClientRepository clientRepository;
    private final CardService cardService;
    private final ClientMapper clientMapper;

    @Override
    public ClientDto createClient(String fullName, LocalDate birthDate, String email, String phone, String passportNumber) {
        Optional<Client> existingClient = clientRepository.findByFullNameAndBirthDate(fullName,birthDate);

        if(existingClient.isPresent()){
            // Важно!!!
            // Проверяется существование клиента в базе данных только по ФИО и дате рождения
            // Если указаны другая почта, номер телефона или паспорт, вернется из базы клиент со старыми данными
            // Нужно либо сделать уведомление о том что необходимо изменить данные клиента(думаю так лучше),
            // либо тут же заменить данные
            // TODO
            ClientDto clientDto = clientMapper.toDto(existingClient.get());
            clientDto.setCardIds(cardService.getClientCards(clientDto.getId()).stream()
                    .map(CardDto::getId)
                    .collect(Collectors.toList()));
            return clientDto;
        }

        clientRepository.findByEmail(email).ifPresent(c -> {
            throw new IllegalArgumentException("Email already in use: " + email);
        });

        Client client = new Client(fullName, birthDate, email, passportNumber, phone);
        // При добавлении БД, я понял, что она сама генерирует ID, но при создании клиента(карты) у меня
        // тоже генерируется ID, и получается что ID перезаписывается при сохранении в базу данных.
        // Я думаю, что нужно убрать генерацию ID в моделях и сделать отдельно в репозиториях
        // (но пока мне это не как не мешает, просто перезаписывается id)(также происходит с картами)
        // TODO
        Client saved = clientRepository.save(client);

        ClientDto dto = clientMapper.toDto(saved);
        dto.setCardIds(List.of());

        return dto;
    }

    @Override
    public Optional<ClientDto> getClient(String id) {
        return clientRepository.findById(id)
                .map(client -> {
                    ClientDto clientDto = clientMapper.toDto(client);
                    List<CardDto> cards = cardService.getClientCards(client.getId());
                    clientDto.setCardIds(cards.stream().map(CardDto::getId).collect(Collectors.toList()));
                    return clientDto;
                });
    }

    @Override
    public List<ClientDto> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        List<ClientDto> clientsDto = clientMapper.toDtoList(clients);

        clientsDto.forEach(clientDto -> {
                    List<CardDto> cards = cardService.getClientCards(clientDto.getId());
                    clientDto.setCardIds(cards.stream().map(CardDto::getId).collect(Collectors.toList()));
                });
        return clientsDto;
    }

    @Override
    public Optional<ClientDto> updateEmail(String clientId, String newEmail) {
        Optional<Client> clientOpt = clientRepository.findById(clientId);
        if (clientOpt.isEmpty()) {
            throw new IllegalArgumentException("Client not found: " + clientId);
        }
        Client client = clientOpt.get();

        Optional<Client> clientWithEmail = clientRepository.findByEmail(newEmail);
        if (clientWithEmail.isPresent() && !clientWithEmail.get().getId().equals(clientId)) {
            throw new IllegalArgumentException("Email already in use: " + newEmail);
        }

         clientRepository.updateEmail(clientId,newEmail);

        ClientDto clientDto = clientMapper.toDto(client);
        clientDto.setCardIds(cardService.getClientCards(clientId).stream()
                .map(CardDto::getId)
                .collect(Collectors.toList()));
        return Optional.of(clientDto);
    }

    @Override
    public boolean deleteClient(String id) {
        List<CardDto> activeCards = cardService.getActiveClientCards(id);
        if (!activeCards.isEmpty()) {
            throw new IllegalStateException("Cannot delete client with active cards.");
        }
        return clientRepository.deleteById(id);
    }

    @Override
    public boolean clientExists(String id) {
        return clientRepository.existsById(id);
    }

    @Override
    public Optional<ClientDto> findClientByEmail(String email) {
        return clientRepository.findByEmail(email)
                .map(client -> {
                    ClientDto clientDto = clientMapper.toDto(client);
                    List<CardDto> cards = cardService.getClientCards(client.getId());
                    clientDto.setCardIds(cards.stream().map(CardDto::getId).collect(Collectors.toList()));
                    return clientDto;
                });
    }

    @Override
    public Optional<ClientDto> findClientByFullNameAndBirthDate(String fullName, LocalDate birthDate) {
        return clientRepository.findByFullNameAndBirthDate(fullName,birthDate)
                .map(client -> {
                    ClientDto clientDto = clientMapper.toDto(client);
                    List<CardDto> cards = cardService.getClientCards(client.getId());
                    clientDto.setCardIds(cards.stream().map(CardDto::getId).collect(Collectors.toList()));
                    return clientDto;
                });
    }
}
