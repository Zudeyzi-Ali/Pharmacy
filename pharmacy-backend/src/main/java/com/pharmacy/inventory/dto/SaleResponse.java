package com.pharmacy.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class SaleResponse {
    private UUID id;
    private LocalDateTime saleDate;
    private String pharmacistUsername;
    private BigDecimal totalAmount;
    private List<SaleItemResponse> items;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SaleItemResponse {
        private Long medicineId;
        private String medicineName;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}
