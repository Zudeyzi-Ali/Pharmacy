package com.pharmacy.inventory.dto;

import com.pharmacy.inventory.model.StockAlert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAlertDTO {
    private Long id;
    private Long medicineId;
    private String medicineName;
    private StockAlert.AlertType alertType;
    private String message;
    private boolean resolved;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
