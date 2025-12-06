package org.example.smartshop.entities.models;

import jakarta.persistence.*;
import lombok.*;
import org.example.smartshop.entities.enums.PaymentStatus;
import org.example.smartshop.entities.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "paiements")
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Commande commande;

    private Integer numeroPaiement;
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    private PaymentType typePaiement;

    private LocalDateTime datePaiement;
    private LocalDateTime dateEncaissement;

    @Enumerated(EnumType.STRING)
    private PaymentStatus statut;

    // Nouveaux attributs selon le cahier des charges
    private String reference;  // Numéro de chèque, référence de virement, numéro de reçu
    private String banque;     // Nom de la banque (pour chèque et virement)
    private LocalDateTime dateEcheance;  // Date d'échéance (pour chèque)
}