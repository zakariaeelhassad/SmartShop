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

    private int numeroPaiement;
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    private PaymentType typePaiement;

    private LocalDateTime datePaiement;
    private LocalDateTime dateEncaissement;

    @Enumerated(EnumType.STRING)
    private PaymentStatus statut;
}
