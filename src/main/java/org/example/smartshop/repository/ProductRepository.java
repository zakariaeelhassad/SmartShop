package org.example.smartshop.repository;

import org.example.smartshop.entities.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
