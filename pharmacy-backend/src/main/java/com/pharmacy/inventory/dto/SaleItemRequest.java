package com.pharmacy.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleItemRequest {

    @NotNull
    private Long medicineId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
