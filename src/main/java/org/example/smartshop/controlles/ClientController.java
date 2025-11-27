package org.example.smartshop.controlles;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartshop.entities.DTO.clients.ClientCreateDto;
import org.example.smartshop.entities.DTO.clients.ClientResponseDto;
import org.example.smartshop.entities.DTO.clients.ClientUpdateDto;
import org.example.smartshop.services.IClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final IClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponseDto> createClient(@Valid @RequestBody ClientCreateDto dto) {
        ClientResponseDto created = clientService.createClient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDto>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/me")
    public ResponseEntity<ClientResponseDto> getCurrentClient() {
        return ResponseEntity.ok(clientService.getCurrentClientInfo());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDto> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientUpdateDto dto
    ) {
        return ResponseEntity.ok(clientService.updateClient(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.ok().build();
    }
}