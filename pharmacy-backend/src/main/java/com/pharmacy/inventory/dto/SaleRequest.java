package com.pharmacy.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SaleRequest {

    @NotEmpty
    @Valid
    private List<SaleItemRequest> items;
}
