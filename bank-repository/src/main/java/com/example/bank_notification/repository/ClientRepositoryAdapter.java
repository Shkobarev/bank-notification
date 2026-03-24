package com.example.bank_notification.repository;

import com.example.bank_notification.entity.CardEntity;
import com.example.bank_notification.entity.ClientEntity;
import com.example.bank_notification.model.Client;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "jpa")
@RequiredArgsConstructor
public class ClientRepositoryAdapter implements ClientRepository{
    private final ClientJpaRepository jpaRepository;
    private final CardJpaRepository cardJpaRepository;

    @Override
    @Transactional
    public Client save(Client client) {
        if (client.getId() != null && jpaRepository.existsById(client.getId())) {
            ClientEntity existing = jpaRepository.findById(client.getId()).get();
            existing.setFullName(client.getFullName());
            existing.setBirthDate(client.getBirthDate());
            existing.setEmail(client.getEmail());
            existing.setPassportNumber(client.getPassportNumber());
            existing.setPhone(client.getPhone());
            return toModel(jpaRepository.save(existing));
        }

        ClientEntity entity = new ClientEntity(
                client.getFullName(),
                client.getBirthDate(),
                client.getEmail(),
                client.getPassportNumber(),
                client.getPhone()
        );
        ClientEntity saved = jpaRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public Optional<Client> findById(String id) {
        return jpaRepository.findById(id).map(this::toModel);
    }

    @Override
    public Optional<Client> findByFullNameAndBirthDate(String fullName, LocalDate birthDate) {
        return jpaRepository.findByFullNameAndBirthDate(fullName, birthDate).map(this::toModel);
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toModel);
    }

    @Override
    @Transactional
    public Optional<Client> updateEmail(String clientId, String newEmail) {
        return jpaRepository.findById(clientId)
                .map(entity -> {
                    entity.setEmail(newEmail);
                    return jpaRepository.save(entity);
                })
                .map(this::toModel);
    }

    @Override
    public List<Client> findAll() {
        return jpaRepository.findAll().stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteById(String id) {
        if(jpaRepository.existsById(id)){
            jpaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public boolean existsById(String id) {
        return jpaRepository.existsById(id);
    }

    private Client toModel(ClientEntity entity) {
        if (entity == null) return null;

        Client client = new Client(
                entity.getFullName(),
                entity.getBirthDate(),
                entity.getEmail(),
                entity.getPassportNumber(),
                entity.getPhone()
        );
        client.setId(entity.getId());
        client.setCreatedAt(entity.getCreatedAt());
        return client;
    }
}
