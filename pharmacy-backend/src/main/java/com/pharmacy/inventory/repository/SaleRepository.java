package com.pharmacy.inventory.repository;

import com.pharmacy.inventory.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SaleRepository extends JpaRepository<Sale, UUID> {

    @Query("SELECT s FROM Sale s WHERE s.saleDate >= :start AND s.saleDate < :end")
    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);
}
