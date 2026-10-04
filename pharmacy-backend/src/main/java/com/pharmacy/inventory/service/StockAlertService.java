package com.pharmacy.inventory.service;

import com.pharmacy.inventory.dto.StockAlertDTO;
import com.pharmacy.inventory.exception.ApiException;
import com.pharmacy.inventory.model.Medicine;
import com.pharmacy.inventory.model.StockAlert;
import com.pharmacy.inventory.repository.MedicineRepository;
import com.pharmacy.inventory.repository.StockAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StockAlertService {

    private static final int EXPIRY_WARNING_DAYS = 30;

    private final StockAlertRepository stockAlertRepository;
    private final MedicineRepository medicineRepository;

    public List<StockAlertDTO> getActiveAlerts() {
        return stockAlertRepository.findByResolvedFalseOrderByCreatedAtDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public StockAlertDTO resolveAlert(Long id) {
        StockAlert alert = stockAlertRepository.findById(id)
                .orElseThrow(() -> new ApiException("Alert not found with id: " + id, HttpStatus.NOT_FOUND));
        alert.setResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        return toDTO(stockAlertRepository.save(alert));
    }

    /**
     * Core check run by the scheduled job. Creates a new alert only if there
     * isn't already an unresolved alert of the same type for that medicine.
     */
    public void runStockAndExpiryCheck() {
        checkLowStock();
        checkNearExpiry();
    }

    private void checkLowStock() {
        List<Medicine> lowStock = medicineRepository.findLowStockMedicines();
        for (Medicine medicine : lowStock) {
            boolean alreadyAlerted = stockAlertRepository
                    .findByMedicine_IdAndAlertTypeAndResolvedFalse(medicine.getId(), StockAlert.AlertType.LOW_STOCK)
                    .isPresent();
            if (!alreadyAlerted) {
                StockAlert alert = StockAlert.builder()
                        .medicine(medicine)
                        .alertType(StockAlert.AlertType.LOW_STOCK)
                        .message(String.format("Low stock: %s has %d units left (reorder level: %d)",
                                medicine.getName(), medicine.getQuantity(), medicine.getReorderLevel()))
                        .resolved(false)
                        .build();
                stockAlertRepository.save(alert);
                log.info("Created LOW_STOCK alert for medicine id={}", medicine.getId());
            }
        }
    }

    private void checkNearExpiry() {
        LocalDate threshold = LocalDate.now().plusDays(EXPIRY_WARNING_DAYS);
        List<Medicine> nearExpiry = medicineRepository.findMedicinesNearingExpiry(threshold);
        for (Medicine medicine : nearExpiry) {
            boolean alreadyAlerted = stockAlertRepository
                    .findByMedicine_IdAndAlertTypeAndResolvedFalse(medicine.getId(), StockAlert.AlertType.NEAR_EXPIRY)
                    .isPresent();
            if (!alreadyAlerted) {
                StockAlert alert = StockAlert.builder()
                        .medicine(medicine)
                        .alertType(StockAlert.AlertType.NEAR_EXPIRY)
                        .message(String.format("%s expires on %s (within %d days)",
                                medicine.getName(), medicine.getExpiryDate(), EXPIRY_WARNING_DAYS))
                        .resolved(false)
                        .build();
                stockAlertRepository.save(alert);
                log.info("Created NEAR_EXPIRY alert for medicine id={}", medicine.getId());
            }
        }
    }

    private StockAlertDTO toDTO(StockAlert alert) {
        return StockAlertDTO.builder()
                .id(alert.getId())
                .medicineId(alert.getMedicine().getId())
                .medicineName(alert.getMedicine().getName())
                .alertType(alert.getAlertType())
                .message(alert.getMessage())
                .resolved(alert.isResolved())
                .createdAt(alert.getCreatedAt())
                .resolvedAt(alert.getResolvedAt())
                .build();
    }
}
