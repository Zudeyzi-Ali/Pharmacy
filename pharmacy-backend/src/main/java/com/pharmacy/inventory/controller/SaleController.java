package com.pharmacy.inventory.controller;

import com.pharmacy.inventory.dto.ApiResponse;
import com.pharmacy.inventory.dto.MonthlySalesReportResponse;
import com.pharmacy.inventory.dto.SaleRequest;
import com.pharmacy.inventory.dto.SaleResponse;
import com.pharmacy.inventory.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    // Processing a sale at the register (POS) is routine pharmacist work - either role.
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    @PostMapping
    public ResponseEntity<ApiResponse<SaleResponse>> create(
            @Valid @RequestBody SaleRequest request,
            Authentication authentication) {
        SaleResponse response = saleService.create(request, authentication);
        return ResponseEntity.ok(ApiResponse.ok("Sale recorded", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SaleResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(saleService.findAll()));
    }

    // Monthly revenue/reporting is management-facing - ADMIN only.
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reports/monthly")
    public ResponseEntity<ApiResponse<MonthlySalesReportResponse>> monthlyReport(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(ApiResponse.ok(saleService.monthlyReport(year, month)));
    }
}
