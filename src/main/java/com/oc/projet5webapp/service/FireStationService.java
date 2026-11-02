package com.oc.projet5webapp.service;

import com.oc.projet5webapp.model.FireStation;
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
public class FireStationService {

    private final ObjectMapper objectMapper;
    private final Path dataFile = Path.of("data.json");

    public FireStationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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
     *         or an empty Optional if no matching fire station exists
     * @throws NullPointerException if fire station is null
     */
    public Optional<FireStation> updateFireStation(@Nonnull FireStation fireStation) {
        //Validate fire station
        Objects.requireNonNull(fireStation, "Fire station must not be null");
        // Read file
        List<FireStation> fireStations = readFireStations();

        // Search for the fire station
        Optional<FireStation> existingFireStation = findFireStation(fireStations, fireStation.getAddress(), fireStation.getStation());
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
     * @param address the address of the fire station to delete
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

}
