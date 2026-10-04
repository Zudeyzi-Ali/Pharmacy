package com.pharmacy.inventory.controller;

import com.pharmacy.inventory.dto.ApiResponse;
import com.pharmacy.inventory.dto.StockAlertDTO;
import com.pharmacy.inventory.service.StockAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class StockAlertController {

    private final StockAlertService stockAlertService;

    /** List all active (unresolved) alerts, used by the dashboard widget. Any logged-in user. */
    @GetMapping
    public ResponseEntity<ApiResponse<List<StockAlertDTO>>> getActiveAlerts() {
        return ResponseEntity.ok(ApiResponse.ok(stockAlertService.getActiveAlerts()));
    }

    /** Mark an alert as resolved - part of normal day-to-day stock handling, either role. */
    @PutMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<StockAlertDTO>> resolveAlert(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Alert resolved", stockAlertService.resolveAlert(id)));
    }

    /** Manually trigger the low-stock/near-expiry check (testing/demo utility) - ADMIN only. */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/check-now")
    public ResponseEntity<ApiResponse<Void>> runCheckNow() {
        stockAlertService.runStockAndExpiryCheck();
        return ResponseEntity.ok(ApiResponse.ok("Check completed", null));
    }
}
