package com.pharmacy.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MonthlySalesReportResponse {
    private int year;
    private int month;
    private long totalSalesCount;
    private BigDecimal totalRevenue;
    private List<TopMedicine> topSellingMedicines;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TopMedicine {
        private Long medicineId;
        private String medicineName;
        private long totalQuantitySold;
    }
}
