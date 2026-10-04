package com.pharmacy.inventory.controller;

import com.pharmacy.inventory.dto.ApiResponse;
import com.pharmacy.inventory.dto.MedicineDTO;
import com.pharmacy.inventory.service.MedicineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * NOTE: this replaces the read-only skeleton MedicineController already in the
 * shared repo (which only had GET all / GET by id). This version adds full
 * CRUD + search/filter, matching Member 2's spec.
 */
@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    /**
     * GET /api/medicines                       -> all medicines
     * GET /api/medicines?name=para              -> search by name
     * GET /api/medicines?categoryId=2           -> filter by category
     * GET /api/medicines?name=para&categoryId=2 -> search + filter combined
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicineDTO>>> getMedicines(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(ApiResponse.ok(medicineService.searchMedicines(name, categoryId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicineDTO>> getMedicineById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(medicineService.getMedicineById(id)));
    }

    // Adding/editing stock records is an ADMIN-only action.
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<MedicineDTO>> createMedicine(@Valid @RequestBody MedicineDTO dto) {
        return new ResponseEntity<>(ApiResponse.ok("Medicine created", medicineService.createMedicine(dto)), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicineDTO>> updateMedicine(@PathVariable Long id, @Valid @RequestBody MedicineDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Medicine updated", medicineService.updateMedicine(id, dto)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMedicine(@PathVariable Long id) {
        medicineService.deleteMedicine(id);
        return ResponseEntity.ok(ApiResponse.ok("Medicine deleted", null));
    }
}
