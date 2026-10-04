package com.pharmacy.inventory.repository;

import com.pharmacy.inventory.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    List<Medicine> findByNameContainingIgnoreCase(String name);

    List<Medicine> findByCategory_Id(Long categoryId);

    List<Medicine> findByNameContainingIgnoreCaseAndCategory_Id(String name, Long categoryId);

    @Query("SELECT m FROM Medicine m WHERE m.quantity < m.reorderLevel")
    List<Medicine> findLowStockMedicines();

    @Query("SELECT m FROM Medicine m WHERE m.expiryDate <= :thresholdDate")
    List<Medicine> findMedicinesNearingExpiry(@Param("thresholdDate") LocalDate thresholdDate);
}
