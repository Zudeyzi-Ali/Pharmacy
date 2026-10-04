package com.pharmacy.inventory.service;

import com.pharmacy.inventory.dto.MedicineDTO;
import com.pharmacy.inventory.exception.ApiException;
import com.pharmacy.inventory.model.Category;
import com.pharmacy.inventory.model.Medicine;
import com.pharmacy.inventory.repository.CategoryRepository;
import com.pharmacy.inventory.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * NOTE: this replaces the read-only skeleton MedicineService already in the shared
 * repo (which only had getAllMedicines/getMedicineById). This version adds full
 * CRUD + search/filter, matching Member 2's spec.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final CategoryRepository categoryRepository;

    public List<MedicineDTO> getAllMedicines() {
        return medicineRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MedicineDTO getMedicineById(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ApiException("Medicine not found with id: " + id, HttpStatus.NOT_FOUND));
        return toDTO(medicine);
    }

    /**
     * Search medicines by name (partial, case-insensitive) and/or filter by category.
     * Either parameter may be null.
     */
    public List<MedicineDTO> searchMedicines(String name, Long categoryId) {
        List<Medicine> results;

        boolean hasName = StringUtils.hasText(name);
        boolean hasCategory = categoryId != null;

        if (hasName && hasCategory) {
            results = medicineRepository.findByNameContainingIgnoreCaseAndCategory_Id(name, categoryId);
        } else if (hasName) {
            results = medicineRepository.findByNameContainingIgnoreCase(name);
        } else if (hasCategory) {
            results = medicineRepository.findByCategory_Id(categoryId);
        } else {
            results = medicineRepository.findAll();
        }

        return results.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MedicineDTO createMedicine(MedicineDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ApiException("Category not found with id: " + dto.getCategoryId(), HttpStatus.NOT_FOUND));

        Medicine medicine = Medicine.builder()
                .name(dto.getName())
                .batchNumber(dto.getBatchNumber())
                .category(category)
                .quantity(dto.getQuantity())
                .reorderLevel(dto.getReorderLevel())
                .expiryDate(dto.getExpiryDate())
                .price(dto.getPrice())
                .supplier(dto.getSupplier())
                .build();

        return toDTO(medicineRepository.save(medicine));
    }

    public MedicineDTO updateMedicine(Long id, MedicineDTO dto) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ApiException("Medicine not found with id: " + id, HttpStatus.NOT_FOUND));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ApiException("Category not found with id: " + dto.getCategoryId(), HttpStatus.NOT_FOUND));

        medicine.setName(dto.getName());
        medicine.setBatchNumber(dto.getBatchNumber());
        medicine.setCategory(category);
        medicine.setQuantity(dto.getQuantity());
        medicine.setReorderLevel(dto.getReorderLevel());
        medicine.setExpiryDate(dto.getExpiryDate());
        medicine.setPrice(dto.getPrice());
        medicine.setSupplier(dto.getSupplier());

        return toDTO(medicineRepository.save(medicine));
    }

    public void deleteMedicine(Long id) {
        if (!medicineRepository.existsById(id)) {
            throw new ApiException("Medicine not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        medicineRepository.deleteById(id);
    }

    private MedicineDTO toDTO(Medicine medicine) {
        return MedicineDTO.builder()
                .id(medicine.getId())
                .name(medicine.getName())
                .batchNumber(medicine.getBatchNumber())
                .categoryId(medicine.getCategory() != null ? medicine.getCategory().getId() : null)
                .categoryName(medicine.getCategory() != null ? medicine.getCategory().getName() : null)
                .quantity(medicine.getQuantity())
                .reorderLevel(medicine.getReorderLevel())
                .expiryDate(medicine.getExpiryDate())
                .price(medicine.getPrice())
                .supplier(medicine.getSupplier())
                .build();
    }
}
