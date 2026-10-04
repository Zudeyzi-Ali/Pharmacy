package com.pharmacy.inventory.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * NOTE: this replaces the read-only skeleton MedicineDTO that was already in the
 * shared repo (id/name/categoryName/quantity/price/expiryDate only). This version
 * adds the fields needed for full CRUD (categoryId, batchNumber, reorderLevel,
 * supplier) plus validation annotations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineDTO {
    private Long id;

    @NotBlank(message = "Medicine name is required")
    private String name;

    private String batchNumber;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private String categoryName;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @NotNull(message = "Reorder level is required")
    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date should be in the future")
    private LocalDate expiryDate;

    private BigDecimal price;

    private String supplier;
}
