package org.example.smartshop.entities.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.smartshop.entities.enums.CustomerTier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String email;

    @Enumerated(EnumType.STRING)
    private CustomerTier tier;

    private int totalOrders;
    private BigDecimal totalSpent;

    private LocalDateTime firstOrderDate;
    private LocalDateTime lastOrderDate;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "client")
    private List<Commande> commandes;
}
