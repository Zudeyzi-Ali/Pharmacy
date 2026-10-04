package com.pharmacy.inventory.service;

import com.pharmacy.inventory.dto.MonthlySalesReportResponse;
import com.pharmacy.inventory.dto.SaleRequest;
import com.pharmacy.inventory.dto.SaleResponse;
import com.pharmacy.inventory.exception.ApiException;
import com.pharmacy.inventory.model.Medicine;
import com.pharmacy.inventory.model.Sale;
import com.pharmacy.inventory.model.SaleItem;
import com.pharmacy.inventory.model.User;
import com.pharmacy.inventory.repository.MedicineRepository;
import com.pharmacy.inventory.repository.SaleItemRepository;
import com.pharmacy.inventory.repository.SaleRepository;
import com.pharmacy.inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;

    public SaleResponse create(SaleRequest request, Authentication authentication) {
        User pharmacist = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.UNAUTHORIZED));

        Sale sale = Sale.builder()
                .pharmacist(pharmacist)
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<SaleItem> items = request.getItems().stream()
                .map(itemReq -> {
                    Medicine medicine = medicineRepository.findById(itemReq.getMedicineId())
                            .orElseThrow(() -> new ApiException("Medicine not found", HttpStatus.NOT_FOUND));

                    if (medicine.getQuantity() < itemReq.getQuantity()) {
                        throw new ApiException(
                                "Insufficient stock for " + medicine.getName()
                                        + " (available: " + medicine.getQuantity() + ")",
                                HttpStatus.BAD_REQUEST);
                    }

                    medicine.setQuantity(medicine.getQuantity() - itemReq.getQuantity());
                    medicineRepository.save(medicine);

                    return SaleItem.builder()
                            .sale(sale)
                            .medicine(medicine)
                            .quantity(itemReq.getQuantity())
                            .unitPrice(medicine.getPrice())
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        sale.setItems(items);
        sale.setTotalAmount(total);

        Sale saved = saleRepository.save(sale);
        return toResponse(saved);
    }

    public List<SaleResponse> findAll() {
        return saleRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public MonthlySalesReportResponse monthlyReport(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<Sale> sales = saleRepository.findBySaleDateBetween(start, end);

        BigDecimal totalRevenue = sales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<MonthlySalesReportResponse.TopMedicine> topMedicines =
                saleItemRepository.findTopSellingMedicines(start, end).stream()
                        .map(row -> MonthlySalesReportResponse.TopMedicine.builder()
                                .medicineId(row.getMedicineId())
                                .medicineName(row.getMedicineName())
                                .totalQuantitySold(row.getTotalQuantity())
                                .build())
                        .collect(Collectors.toList());

        return MonthlySalesReportResponse.builder()
                .year(year)
                .month(month)
                .totalSalesCount(sales.size())
                .totalRevenue(totalRevenue)
                .topSellingMedicines(topMedicines)
                .build();
    }

    private SaleResponse toResponse(Sale s) {
        List<SaleResponse.SaleItemResponse> items = s.getItems().stream()
                .map(i -> SaleResponse.SaleItemResponse.builder()
                        .medicineId(i.getMedicine().getId())
                        .medicineName(i.getMedicine().getName())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .build())
                .collect(Collectors.toList());

        return SaleResponse.builder()
                .id(s.getId())
                .saleDate(s.getSaleDate())
                .pharmacistUsername(s.getPharmacist().getUsername())
                .totalAmount(s.getTotalAmount())
                .items(items)
                .build();
    }
}
