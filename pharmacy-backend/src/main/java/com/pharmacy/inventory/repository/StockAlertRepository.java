package com.pharmacy.inventory.repository;

import com.pharmacy.inventory.model.StockAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {

    List<StockAlert> findByResolvedFalseOrderByCreatedAtDesc();

    Optional<StockAlert> findByMedicine_IdAndAlertTypeAndResolvedFalse(Long medicineId, StockAlert.AlertType alertType);
}
