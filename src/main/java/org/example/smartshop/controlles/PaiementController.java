package org.example.smartshop.controlles;

import lombok.RequiredArgsConstructor;
import org.example.smartshop.entities.DTO.paiments.PaiementCreateDto;
import org.example.smartshop.entities.DTO.paiments.PaiementResponseDto;
import org.example.smartshop.services.IPaiementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final IPaiementService paiementService;

    @PostMapping
    public ResponseEntity<PaiementResponseDto> createPaiement(@RequestBody PaiementCreateDto createDto) {
        return ResponseEntity.ok(paiementService.createPaiement(createDto));
    }

    @GetMapping("/commande/{commandeId}")
    public ResponseEntity<List<PaiementResponseDto>> getPaiementsByCommande(@PathVariable Long commandeId) {
        return ResponseEntity.ok(paiementService.getPaiementsByCommande(commandeId));
    }

    @GetMapping("/{paiementId}")
    public ResponseEntity<PaiementResponseDto> getPaiementById(@PathVariable Long paiementId) {
        return ResponseEntity.ok(paiementService.getPaiementById(paiementId));
    }

    @PostMapping("/{paiementId}/encaisser")
    public ResponseEntity<PaiementResponseDto> encaisserPaiement(@PathVariable Long paiementId) {
        return ResponseEntity.ok(paiementService.encaisserPaiement(paiementId));
    }

    @PostMapping("/{paiementId}/rejeter")
    public ResponseEntity<PaiementResponseDto> rejeterPaiement(@PathVariable Long paiementId) {
        return ResponseEntity.ok(paiementService.rejeterPaiement(paiementId));
    }

    @GetMapping
    public ResponseEntity<List<PaiementResponseDto>> getAllPaiements() {
        return ResponseEntity.ok(paiementService.getAllPaiements());
    }
}