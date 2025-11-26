package org.example.smartshop.repository;

import org.example.smartshop.entities.models.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaiementRepository extends JpaRepository<Paiement , Long> {
}
