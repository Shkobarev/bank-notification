package com.example.bank_notification.controller;

import com.example.bank_notification.dto.ClientDto;
import com.example.bank_notification.dto.request.CreateClientRequest;
import com.example.bank_notification.dto.request.UpdateEmailRequest;
import com.example.bank_notification.dto.ClientCreationResult;
import com.example.bank_notification.exception.ResourceNotFoundException;
import com.example.bank_notification.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientDto> createClient(@Valid @RequestBody CreateClientRequest request){
        ClientCreationResult result = clientService.createClientWithResult(
                request.getFullName(),
                request.getBirthDate(),
                request.getEmail(),
                request.getPhone(),
                request.getPassportNumber());
        if(result.isCreated()){
            return ResponseEntity.status(HttpStatus.CREATED).body(result.getClientDto());
        }
        else { return ResponseEntity.ok(result.getClientDto()); }
    }

    @GetMapping("/{id}")
    public ClientDto getClientId(@PathVariable String id){
        return clientService.getClient(id)
                .orElseThrow(()-> new ResourceNotFoundException("Client not found with id: " + id));
    }

    @GetMapping
    public List<ClientDto> getAllClients(){
        return clientService.getAllClients();
    }

    @PutMapping("/{id}/email")
    public ClientDto updateEmail(
            @PathVariable String id,
            @Valid @RequestBody UpdateEmailRequest request) {

        return clientService.updateEmail(id, request.getNewEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable String id) {
        if (!clientService.deleteClient(id)) {
            throw new ResourceNotFoundException("Client not found with id: " + id);
        }
    }

    @GetMapping("/search")
    public ClientDto searchByEmail(@RequestParam String email) {
        return clientService.findClientByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with email: " + email));
    }
}
