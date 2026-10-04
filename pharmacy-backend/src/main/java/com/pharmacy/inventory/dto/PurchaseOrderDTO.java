package com.pharmacy.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderDTO {

    private Long id;

    @NotNull(message = "Supplier id is required")
    private Long supplierId;

    private String supplierName;

    private LocalDate orderDate;

    private LocalDate expectedDeliveryDate;

    private String status;

    private BigDecimal totalAmount;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<PurchaseOrderItemDTO> items;
}
