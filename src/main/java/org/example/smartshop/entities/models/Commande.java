package org.example.smartshop.entities.models;

import jakarta.persistence.*;
import lombok.*;
import org.example.smartshop.entities.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "commandes")
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Client client;

    private LocalDateTime dateCreated;

    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal tax;
    private BigDecimal total;

    private String promoCode;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal remainingAmount;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private List<Paiement> paiements;
}
