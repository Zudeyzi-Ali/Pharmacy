package com.pharmacy.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateDTO {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "PENDING|APPROVED|DELIVERED|CANCELLED", message = "Status must be one of PENDING, APPROVED, DELIVERED, CANCELLED")
    private String status;
}
