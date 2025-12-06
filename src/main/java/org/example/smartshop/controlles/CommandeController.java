package org.example.smartshop.controlles;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartshop.entities.DTO.commandes.CommandeCreateDto;
import org.example.smartshop.entities.DTO.commandes.CommandeResponseDto;
import org.example.smartshop.entities.DTO.commandes.CommandeUpdateDto;
import org.example.smartshop.services.ICommandeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@RequiredArgsConstructor
@Slf4j
public class CommandeController {

    private final ICommandeService commandeService;

    @PostMapping
    public ResponseEntity<CommandeResponseDto> createCommande(@Valid @RequestBody CommandeCreateDto createDto) {
        log.info("Creating new commande for client ID: {}", createDto.clientId());
        CommandeResponseDto response = commandeService.createCommande(createDto);
        log.info("Commande created successfully with ID: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeResponseDto> getCommandeById(@PathVariable Long id) {
        log.info("Fetching commande with ID: {}", id);
        CommandeResponseDto response = commandeService.getCommandeById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CommandeResponseDto>> getAllCommandes() {
        log.info("Fetching all commandes");
        List<CommandeResponseDto> commandes = commandeService.getAllCommandes();
        log.info("Found {} commandes", commandes.size());
        return ResponseEntity.ok(commandes);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<CommandeResponseDto>> getCommandesByClient(@PathVariable Long clientId) {
        log.info("Fetching commandes for client ID: {}", clientId);
        List<CommandeResponseDto> commandes = commandeService.getCommandesByClient(clientId);
        log.info("Found {} commandes for client {}", commandes.size(), clientId);
        return ResponseEntity.ok(commandes);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CommandeResponseDto> updateCommandeStatus(
            @PathVariable Long id,
            @Valid @RequestBody CommandeUpdateDto updateDto) {
        log.info("Updating status of commande {} to {}", id, updateDto.status());
        CommandeResponseDto response = commandeService.updateCommandeStatus(id, updateDto);
        log.info("Commande {} status updated successfully", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<CommandeResponseDto> confirmCommande(@PathVariable Long id) {
        log.info("Confirming commande with ID: {}", id);
        CommandeResponseDto response = commandeService.confirmCommande(id);
        log.info("Commande {} confirmed successfully", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<CommandeResponseDto> cancelCommande(@PathVariable Long id) {
        log.info("Canceling commande with ID: {}", id);
        CommandeResponseDto response = commandeService.cancelCommande(id);
        log.info("Commande {} canceled successfully", id);
        return ResponseEntity.ok(response);
    }
}