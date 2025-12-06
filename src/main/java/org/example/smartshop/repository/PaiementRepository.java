package org.example.smartshop.repository;

import org.example.smartshop.entities.models.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    List<Paiement> findByCommandeId(Long commandeId);

    @Query("SELECT COALESCE(MAX(p.numeroPaiement), 0) FROM Paiement p WHERE p.commande.id = :commandeId")
    Integer findMaxNumeroPaiementByCommandeId(@Param("commandeId") Long commandeId);

    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p WHERE p.commande.id = :commandeId AND p.statut = 'ENCAISSE'")
    BigDecimal sumEncaissedAmountByCommandeId(@Param("commandeId") Long commandeId);

    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.commande.id = :commandeId")
    Long countByCommandeId(@Param("commandeId") Long commandeId);
}
