package org.example.smartshop.repository;

import org.example.smartshop.entities.enums.OrderStatus;
import org.example.smartshop.entities.models.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

    List<Commande> findByClientId(Long clientId);
    List<Commande> findByClientIdAndStatus(Long clientId, OrderStatus status);

    @Query("SELECT COUNT(c) FROM Commande c WHERE c.client.id = :clientId AND c.status = 'CONFIRMED'")
    Long countConfirmedOrdersByClientId(@Param("clientId") Long clientId);

    @Query("SELECT COALESCE(SUM(c.total), 0) FROM Commande c WHERE c.client.id = :clientId AND c.status = 'CONFIRMED'")
    BigDecimal sumTotalSpentByClientId(@Param("clientId") Long clientId);

    List<Commande> findByStatus(OrderStatus status);
    boolean existsByPromoCode(String promoCode);
}
