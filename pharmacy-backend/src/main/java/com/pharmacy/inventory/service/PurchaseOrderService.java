package com.pharmacy.inventory.service;

import com.pharmacy.inventory.dto.PurchaseOrderDTO;
import com.pharmacy.inventory.dto.PurchaseOrderItemDTO;
import com.pharmacy.inventory.dto.StatusUpdateDTO;
import com.pharmacy.inventory.exception.ApiException;
import com.pharmacy.inventory.model.*;
import com.pharmacy.inventory.repository.MedicineRepository;
import com.pharmacy.inventory.repository.PurchaseOrderRepository;
import com.pharmacy.inventory.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    // This project's inventory entity is Medicine, not a generic Product —
    // there is no Product entity/table in this codebase.
    private final MedicineRepository medicineRepository;

    public List<PurchaseOrderDTO> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PurchaseOrderDTO getPurchaseOrderById(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ApiException("Purchase order not found with id " + id, HttpStatus.NOT_FOUND));
        return toDTO(order);
    }

    @Transactional
    public PurchaseOrderDTO createPurchaseOrder(PurchaseOrderDTO dto) {
        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new ApiException("Supplier not found with id " + dto.getSupplierId(), HttpStatus.NOT_FOUND));

        PurchaseOrder order = new PurchaseOrder();
        order.setSupplier(supplier);
        order.setOrderDate(dto.getOrderDate() != null ? dto.getOrderDate() : java.time.LocalDate.now());
        order.setExpectedDeliveryDate(dto.getExpectedDeliveryDate());
        order.setStatus(OrderStatus.PENDING);

        List<PurchaseOrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (PurchaseOrderItemDTO itemDTO : dto.getItems()) {
            Medicine medicine = medicineRepository.findById(itemDTO.getMedicineId())
                    .orElseThrow(() -> new ApiException("Medicine not found with id " + itemDTO.getMedicineId(), HttpStatus.NOT_FOUND));

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(order);
            item.setMedicine(medicine);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getUnitPrice());
            items.add(item);

            total = total.add(item.getSubtotal());
        }

        order.setItems(items);
        order.setTotalAmount(total);

        return toDTO(purchaseOrderRepository.save(order));
    }

    @Transactional
    public PurchaseOrderDTO updateStatus(Long id, StatusUpdateDTO statusUpdateDTO) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ApiException("Purchase order not found with id " + id, HttpStatus.NOT_FOUND));

        OrderStatus newStatus = OrderStatus.valueOf(statusUpdateDTO.getStatus());

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new ApiException("A delivered purchase order cannot be updated further", HttpStatus.CONFLICT);
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ApiException("A cancelled purchase order cannot be updated further", HttpStatus.CONFLICT);
        }

        if (newStatus == OrderStatus.DELIVERED) {
            for (PurchaseOrderItem item : order.getItems()) {
                Medicine medicine = item.getMedicine();
                medicine.setQuantity(medicine.getQuantity() + item.getQuantity());
                medicineRepository.save(medicine);
            }
        }

        order.setStatus(newStatus);
        return toDTO(purchaseOrderRepository.save(order));
    }

    public void deletePurchaseOrder(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ApiException("Purchase order not found with id " + id, HttpStatus.NOT_FOUND));
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new ApiException("A delivered purchase order cannot be deleted", HttpStatus.CONFLICT);
        }
        purchaseOrderRepository.delete(order);
    }

    private PurchaseOrderDTO toDTO(PurchaseOrder order) {
        List<PurchaseOrderItemDTO> itemDTOs = order.getItems() == null ? new ArrayList<>() :
                order.getItems().stream().map(item -> PurchaseOrderItemDTO.builder()
                        .id(item.getId())
                        .medicineId(item.getMedicine().getId())
                        .medicineName(item.getMedicine().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build()
                ).collect(Collectors.toList());

        return PurchaseOrderDTO.builder()
                .id(order.getId())
                .supplierId(order.getSupplier().getId())
                .supplierName(order.getSupplier().getName())
                .orderDate(order.getOrderDate())
                .expectedDeliveryDate(order.getExpectedDeliveryDate())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .items(itemDTOs)
                .build();
    }
}
