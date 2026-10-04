package com.pharmacy.inventory.repository;

import com.pharmacy.inventory.model.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {

    @Query("""
        SELECT si.medicine.id AS medicineId, si.medicine.name AS medicineName, SUM(si.quantity) AS totalQuantity
        FROM SaleItem si
        WHERE si.sale.saleDate >= :start AND si.sale.saleDate < :end
        GROUP BY si.medicine.id, si.medicine.name
        ORDER BY SUM(si.quantity) DESC
        """)
    List<TopMedicineRow> findTopSellingMedicines(LocalDateTime start, LocalDateTime end);

    interface TopMedicineRow {
        Long getMedicineId();
        String getMedicineName();
        Long getTotalQuantity();
    }
}
