package com.oc.projet5webapp.controller;


import com.oc.projet5webapp.model.MedicalRecord;
import com.oc.projet5webapp.model.Person;
import com.oc.projet5webapp.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    /**
     * Create a new medical record.
     *
     * @param medicalRecord the medical record to create, provided in the request body
     * @return a ResponseEntity containing the created medical record
     *         with HTTP status 200 OK
     */
    @PostMapping
    public ResponseEntity<MedicalRecord> createMedicalRecord(@Valid @RequestBody MedicalRecord medicalRecord) {

        MedicalRecord newMedicalRecord = medicalRecordService.addMedicalrecord(medicalRecord);
        return ResponseEntity.ok(newMedicalRecord);
    }



    /**
     * Update an existing medical record's information.
     * The medical record is identified by the first name and last name of the person's medical record.
     * These fields remain unchanged; only the other supported fields
     * are updated.
     *
     * @param medicalRecord the medical record containing the updated values, provided in the request body
     * @return a ResponseEntity containing the updated medical record
     *         with HTTP status 200 OK, or an empty response
     *         with HTTP status 404 Not Found if the medical record does not exist
     */
    @PutMapping
    public ResponseEntity<MedicalRecord> updateMedicalRecord(@Valid @RequestBody MedicalRecord medicalRecord) {
        Optional<MedicalRecord> updatedMedicalRecord = medicalRecordService.updateMedicalRecord(medicalRecord);
        return updatedMedicalRecord
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    /**
     * Delete a medical record identified by the first name and last name of the person's medical record
     *
     * @param firstName the first name of the person's medical record to delete
     * @param lastName the last name of the person's medical record to delete
     * @return an empty response with HTTP status 204 No Content
     *         if the medical record was deleted, or HTTP status 404 Not Found
     *         if no matching medical record exists
     */
    @DeleteMapping("/{firstName}/{lastName}")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable String firstName, @PathVariable String lastName) {

        boolean deleted = medicalRecordService.deleteMedicalRecord(firstName, lastName);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

}
