package com.pharmacy.inventory.controller;

import com.pharmacy.inventory.dto.ApiResponse;
import com.pharmacy.inventory.dto.PurchaseOrderDTO;
import com.pharmacy.inventory.dto.StatusUpdateDTO;
import com.pharmacy.inventory.service.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    // Read access: any authenticated user (ADMIN or PHARMACIST).
    @GetMapping
    public ResponseEntity<ApiResponse<List<PurchaseOrderDTO>>> getAllPurchaseOrders() {
        return ResponseEntity.ok(ApiResponse.ok(purchaseOrderService.getAllPurchaseOrders()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> getPurchaseOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(purchaseOrderService.getPurchaseOrderById(id)));
    }

    // Either role can raise a purchase order (e.g. a pharmacist flagging a
    // reorder need); approval/fulfilment authority is restricted below.
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> createPurchaseOrder(@Valid @RequestBody PurchaseOrderDTO dto) {
        return new ResponseEntity<>(ApiResponse.ok("Purchase order created", purchaseOrderService.createPurchaseOrder(dto)), HttpStatus.CREATED);
    }

    // Approving, marking delivered (which updates stock), or cancelling an
    // order is an ADMIN-only action.
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateDTO statusUpdateDTO) {
        return ResponseEntity.ok(ApiResponse.ok("Purchase order status updated", purchaseOrderService.updateStatus(id, statusUpdateDTO)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePurchaseOrder(@PathVariable Long id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.ok(ApiResponse.ok("Purchase order deleted", null));
    }
}
