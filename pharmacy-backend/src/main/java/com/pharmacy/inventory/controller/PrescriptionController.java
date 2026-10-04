package com.pharmacy.inventory.controller;

import com.pharmacy.inventory.dto.ApiResponse;
import com.pharmacy.inventory.dto.PrescriptionRequest;
import com.pharmacy.inventory.dto.PrescriptionResponse;
import com.pharmacy.inventory.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    // Recording a prescription is routine pharmacist work - either role.
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    @PostMapping
    public ResponseEntity<ApiResponse<PrescriptionResponse>> create(
            @Valid @RequestBody PrescriptionRequest request,
            Authentication authentication) {
        PrescriptionResponse response = prescriptionService.create(request, authentication);
        return ResponseEntity.ok(ApiResponse.ok("Prescription recorded", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(prescriptionService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(prescriptionService.findById(id)));
    }
}
