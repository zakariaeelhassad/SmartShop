package org.example.smartshop.repository;

import org.example.smartshop.entities.models.Commande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
    List<Commande> findByClientId(Long clientId);
}
