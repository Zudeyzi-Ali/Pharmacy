package com.pharmacy.inventory.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDTO {

    private Long id;

    @NotBlank(message = "Supplier name is required")
    @Size(max = 100, message = "Supplier name must be under 100 characters")
    private String name;

    @Size(max = 100, message = "Contact person name must be under 100 characters")
    private String contactPerson;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,15}$", message = "Phone number must be valid")
    private String phone;

    @Size(max = 255, message = "Address must be under 255 characters")
    private String address;

    private boolean active;
}
