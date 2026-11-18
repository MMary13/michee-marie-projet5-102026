package com.oc.projet5webapp.service;

import com.oc.projet5webapp.dto.FireStationPerson;
import com.oc.projet5webapp.dto.FireStationResponse;
import com.oc.projet5webapp.model.FireStation;
import com.oc.projet5webapp.model.MedicalRecord;
import com.oc.projet5webapp.model.Person;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
public class FireStationService {

    private final ObjectMapper objectMapper;
    private final PersonService personService;
    private final MedicalRecordService medicalRecordService;
    private final Path dataFile;


    public FireStationService(ObjectMapper objectMapper, PersonService personService,
                              MedicalRecordService medicalRecordService, @Value("${app.data-file}") String dataFilePath) {

        this.objectMapper = objectMapper;
        this.personService = personService;
        this.medicalRecordService = medicalRecordService;
        this.dataFile = Path.of(dataFilePath);
    }

    public FireStationResponse getPersonsForOneStation(int stationNumber) {
        // get the addresses from the station number
        List<String> addresses = getStationAddresses(stationNumber);
        // get a list of persons for these addresses
        List<Person> persons = addresses.stream()
                .flatMap(address -> personService.getPersons(address).stream())
                .toList();

        // get all medical records
        List<MedicalRecord> medicalRecords = medicalRecordService.getMedicalRecords();
        List<FireStationPerson> stationPersons = new ArrayList<>();

        int adultCount = 0;
        int childCount = 0;

        for (Person person : persons) {

            stationPersons.add(new FireStationPerson(
                    person.getFirstName(),
                    person.getLastName(),
                    person.getAddress(),
                    person.getPhone()
            ));

            Optional<MedicalRecord> medicalRecord = medicalRecords.stream()
                    .filter(medRecord ->
                            medRecord.getFirstName().equals(person.getFirstName())
                                    && medRecord.getLastName().equals(person.getLastName()))
                    .findFirst();

            if (medicalRecord.isPresent() && medicalRecord.get().getBirthdate() != null) {

                int age = Period.between(medicalRecord.get().getBirthdate(), LocalDate.now()).getYears();

                if (age <= 18) {
                    childCount++;
                } else {
                    adultCount++;
                }
            }
        }

        return new FireStationResponse(stationPersons, adultCount, childCount);
    }

    /**
     * Adds a fire station to the JSON data file.
     * Existing fire stations and other JSON properties are preserved.
     *
     * @param fireStation the fire station to add
     * @return the added fire station
     * @throws NullPointerException if fire station is null
     */
    public FireStation addFirestation(FireStation fireStation) {
        //Validate fire station
        Objects.requireNonNull(fireStation, "Fire station must not be null");

        // read file
        List<FireStation> fireStations = readFireStations();
        // add the fire station
        fireStations.add(fireStation);

        // update dataFile
        updateFireStationInNodeFile(fireStations);
        return fireStation;
    }

    /**
     * Updates an existing fire station's number station
     *
     * @param fireStation the fire station containing the updated number station
     * @return an Optional containing the updated fire station,
     * or an empty Optional if no matching fire station exists
     * @throws NullPointerException if fire station is null
     */
    public Optional<FireStation> updateFireStation(@Nonnull FireStation fireStation) {
        //Validate fire station
        Objects.requireNonNull(fireStation, "Fire station must not be null");
        // Read file
        List<FireStation> fireStations = readFireStations();

        // Search for the fire station
        Optional<FireStation> existingFireStation = fireStations.stream()
                .filter(f -> Objects.equals(
                        f.getAddress(), fireStation.getAddress()))
                .findFirst();

        if (existingFireStation.isEmpty()) {
            return Optional.empty();
        }
        // Update the fire station in the list
        FireStation fireStationToUpdate = existingFireStation.get();
        fireStationToUpdate.setStation(fireStation.getStation());

        //Update file
        updateFireStationInNodeFile(fireStations);

        return Optional.of(fireStationToUpdate);
    }

    /**
     * Deletes a fire station identified by its address and station number.
     *
     * @param address       the address of the fire station to delete
     * @param stationNumber the station number of the fire station to delete
     * @return true if a fire station was deleted, false if no matching fire station exists
     */
    public boolean deleteFireStation(String address, int stationNumber) {
        // Read file
        List<FireStation> fireStations = readFireStations();

        // Search for the fire station
        Optional<FireStation> existingFireStation = findFireStation(fireStations, address, stationNumber);

        // Return false if the fire station to be deleted isn't found in the file
        if (existingFireStation.isEmpty()) {
            return false;
        }
        // Remove the fire station
        fireStations.remove(existingFireStation.get());
        //Update file
        updateFireStationInNodeFile(fireStations);
        return true;
    }

    private Optional<FireStation> findFireStation(
            List<FireStation> fireStations,
            String address,
            int station) {

        return fireStations.stream()
                .filter(f -> Objects.equals(
                        f.getAddress(), address)
                        && Objects.equals(
                        f.getStation(), station))
                .findFirst();
    }

    private List<FireStation> readFireStations() {
        List<FireStation> fireStations;

        // read the file and get the fire stations node
        JsonNode rootJson = objectMapper.readTree(dataFile.toFile());
        JsonNode fireStationsNode = rootJson.get("firestations");
        if (fireStationsNode == null || !fireStationsNode.isArray()) {
            return new ArrayList<>();
        }

        // convert to java objects
        fireStations = objectMapper.convertValue(
                fireStationsNode,
                new TypeReference<>() {
                }
        );

        return fireStations;
    }

    private void updateFireStationInNodeFile(List<FireStation> fireStations) {
        File file = dataFile.toFile();
        JsonNode root = objectMapper.readTree(file);
        ObjectNode rootObject = (ObjectNode) root;
        rootObject.set("firestations", objectMapper.valueToTree(fireStations));

        // write in file
        objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValue(file, root);
    }

    private List<String> getStationAddresses(int stationNumber) {
        List<FireStation> fireStations = readFireStations();

        return fireStations.stream()
                .filter(f -> Objects.equals(f.getStation(), stationNumber))
                .map(FireStation::getAddress)
                .distinct()
                .toList();
    }

}
