package com.pharmacy.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class PrescriptionResponse {
    private UUID id;
    private String patientName;
    private String doctorName;
    private LocalDate dateIssued;
    private String pharmacistUsername;
    private List<PrescriptionItemResponse> items;
    private LocalDateTime createdAt;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PrescriptionItemResponse {
        private Long medicineId;
        private String medicineName;
        private Integer quantity;
        private String dosageInstructions;
    }
}
