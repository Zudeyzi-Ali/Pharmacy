package com.pharmacy.inventory.service;

import com.pharmacy.inventory.dto.PrescriptionRequest;
import com.pharmacy.inventory.dto.PrescriptionResponse;
import com.pharmacy.inventory.exception.ApiException;
import com.pharmacy.inventory.model.Medicine;
import com.pharmacy.inventory.model.Prescription;
import com.pharmacy.inventory.model.PrescriptionItem;
import com.pharmacy.inventory.model.User;
import com.pharmacy.inventory.repository.MedicineRepository;
import com.pharmacy.inventory.repository.PrescriptionRepository;
import com.pharmacy.inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;

    public PrescriptionResponse create(PrescriptionRequest request, Authentication authentication) {
        User pharmacist = currentUser(authentication);

        Prescription prescription = Prescription.builder()
                .patientName(request.getPatientName())
                .doctorName(request.getDoctorName())
                .dateIssued(request.getDateIssued())
                .pharmacist(pharmacist)
                .build();

        List<PrescriptionItem> items = request.getItems().stream()
                .map(itemReq -> PrescriptionItem.builder()
                        .prescription(prescription)
                        .medicine(findMedicine(itemReq.getMedicineId()))
                        .quantity(itemReq.getQuantity())
                        .dosageInstructions(itemReq.getDosageInstructions())
                        .build())
                .collect(Collectors.toList());

        prescription.setItems(items);

        Prescription saved = prescriptionRepository.save(prescription);
        return toResponse(saved);
    }

    public List<PrescriptionResponse> findAll() {
        return prescriptionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PrescriptionResponse findById(UUID id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ApiException("Prescription not found", HttpStatus.NOT_FOUND));
        return toResponse(prescription);
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.UNAUTHORIZED));
    }

    private Medicine findMedicine(Long medicineId) {
        return medicineRepository.findById(medicineId)
                .orElseThrow(() -> new ApiException("Medicine not found", HttpStatus.NOT_FOUND));
    }

    private PrescriptionResponse toResponse(Prescription p) {
        List<PrescriptionResponse.PrescriptionItemResponse> items = p.getItems().stream()
                .map(i -> PrescriptionResponse.PrescriptionItemResponse.builder()
                        .medicineId(i.getMedicine().getId())
                        .medicineName(i.getMedicine().getName())
                        .quantity(i.getQuantity())
                        .dosageInstructions(i.getDosageInstructions())
                        .build())
                .collect(Collectors.toList());

        return PrescriptionResponse.builder()
                .id(p.getId())
                .patientName(p.getPatientName())
                .doctorName(p.getDoctorName())
                .dateIssued(p.getDateIssued())
                .pharmacistUsername(p.getPharmacist().getUsername())
                .items(items)
                .createdAt(p.getCreatedAt())
                .build();
    }
}
