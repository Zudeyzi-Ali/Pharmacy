package com.pharmacy.inventory.service;

import com.pharmacy.inventory.dto.SupplierDTO;
import com.pharmacy.inventory.exception.ApiException;
import com.pharmacy.inventory.model.Supplier;
import com.pharmacy.inventory.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SupplierDTO getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ApiException("Supplier not found with id " + id, HttpStatus.NOT_FOUND));
        return toDTO(supplier);
    }

    public SupplierDTO createSupplier(SupplierDTO dto) {
        if (supplierRepository.existsByEmail(dto.getEmail())) {
            throw new ApiException("A supplier with this email already exists", HttpStatus.BAD_REQUEST);
        }
        Supplier supplier = new Supplier();
        applyDTO(supplier, dto);
        return toDTO(supplierRepository.save(supplier));
    }

    public SupplierDTO updateSupplier(Long id, SupplierDTO dto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ApiException("Supplier not found with id " + id, HttpStatus.NOT_FOUND));

        supplierRepository.findByEmail(dto.getEmail()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ApiException("A supplier with this email already exists", HttpStatus.BAD_REQUEST);
            }
        });

        applyDTO(supplier, dto);
        return toDTO(supplierRepository.save(supplier));
    }

    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ApiException("Supplier not found with id " + id, HttpStatus.NOT_FOUND));
        supplierRepository.delete(supplier);
    }

    private void applyDTO(Supplier supplier, SupplierDTO dto) {
        supplier.setName(dto.getName());
        supplier.setContactPerson(dto.getContactPerson());
        supplier.setEmail(dto.getEmail());
        supplier.setPhone(dto.getPhone());
        supplier.setAddress(dto.getAddress());
        supplier.setActive(dto.isActive());
    }

    private SupplierDTO toDTO(Supplier supplier) {
        return SupplierDTO.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .contactPerson(supplier.getContactPerson())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .active(supplier.isActive())
                .build();
    }
}
