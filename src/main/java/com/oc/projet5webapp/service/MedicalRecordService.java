package com.oc.projet5webapp.service;


import com.oc.projet5webapp.model.MedicalRecord;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MedicalRecordService {


    private final ObjectMapper objectMapper;
    private final Path dataFile = Path.of("data.json");

    public MedicalRecordService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Adds a medical record to the JSON data file.
     * Existing medical records and other JSON properties are preserved.
     *
     * @param medicalRecord the medical record to add
     * @return the added medical record
     * @throws NullPointerException if medical record is null
     */
    public MedicalRecord addMedicalrecord(MedicalRecord medicalRecord) {
        //Validate medical record
        Objects.requireNonNull(medicalRecord, "Medical record must not be null");

        // read file
        List<MedicalRecord> medicalRecords = readMedicalRecords();
        // add the new medical record
        medicalRecords.add(medicalRecord);

        // update dataFile
        updateMedicalRecordInNodeFile(medicalRecords);
        return medicalRecord;
    }

    /**
     * Updates an existing medical record.
     * The lastname and firstname can't be changed
     *
     * @param medicalRecord the medical record containing the updated data
     * @return an Optional containing the updated medical record,
     *         or an empty Optional if no matching medical record exists
     * @throws NullPointerException if medical record is null
     */
    public Optional<MedicalRecord> updateMedicalRecord(@Nonnull MedicalRecord medicalRecord) {
        //Validate medical Record
        Objects.requireNonNull(medicalRecord, "Medical record must not be null");
        // Read file
        List<MedicalRecord> medicalRecords = readMedicalRecords();

        // Search for the medical record to update
        Optional<MedicalRecord> existingMedicalRecord = findMedicalRecord(medicalRecords, medicalRecord.getFirstName(),medicalRecord.getLastName());
        if (existingMedicalRecord.isEmpty()) {
            return Optional.empty();
        }
        // Update the medical record in the list
        MedicalRecord medicalRecordToUpdate = existingMedicalRecord.get();
        medicalRecordToUpdate.setBirthdate(medicalRecord.getBirthdate());
        medicalRecordToUpdate.setMedications(medicalRecord.getMedications());
        medicalRecordToUpdate.setAllergies(medicalRecord.getAllergies());

        //Update file
        updateMedicalRecordInNodeFile(medicalRecords);

        return Optional.of(medicalRecordToUpdate);
    }

    /**
     * Deletes a medical record identified by the firstname and lastname.
     *
     * @param firstName the firstname of the person's medical record to delete
     * @param lastName the lastname of the person's medical record to delete
     * @return true if a medical record was deleted, false if no matching medical record exists
     */
    public boolean deleteMedicalRecord(String firstName, String lastName) {
        // Read file
        List<MedicalRecord> medicalRecords = readMedicalRecords();

        // Search for the medical record
        Optional<MedicalRecord> existingMedicalRecord = findMedicalRecord(medicalRecords,firstName,lastName);

        // Return false if the medical record to be deleted isn't found in the file
        if (existingMedicalRecord.isEmpty()) {
            return false;
        }
        // Remove the medical record
        medicalRecords.remove(existingMedicalRecord.get());
        //Update file
        updateMedicalRecordInNodeFile(medicalRecords);
        return true;
    }

    private Optional<MedicalRecord> findMedicalRecord(
            List<MedicalRecord> medicalRecords,
            String firstName,
            String lastName) {

        return medicalRecords.stream()
                .filter(m -> Objects.equals(
                        m.getFirstName(), firstName)
                        && Objects.equals(
                        m.getLastName(), lastName))
                .findFirst();
    }

    private List<MedicalRecord> readMedicalRecords() {
        List<MedicalRecord> medicalRecords;

        // read the file and get the medical records node
        JsonNode rootJson = objectMapper.readTree(dataFile.toFile());
        JsonNode medicalRecordsNode = rootJson.get("medicalrecords");
        if (medicalRecordsNode == null || !medicalRecordsNode.isArray()) {
            return new ArrayList<>();
        }

        // convert to java objects
        medicalRecords = objectMapper.convertValue(
                medicalRecordsNode,
                new TypeReference<>() {
                }
        );

        return medicalRecords;
    }

    private void updateMedicalRecordInNodeFile(List<MedicalRecord> medicalRecords) {
        File file = dataFile.toFile();
        JsonNode root = objectMapper.readTree(file);
        ObjectNode rootObject = (ObjectNode) root;
        rootObject.set("medicalrecords", objectMapper.valueToTree(medicalRecords));

        // write in file
        objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValue(file, root);
    }
}
