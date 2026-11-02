package com.oc.projet5webapp.controller;

import com.oc.projet5webapp.model.FireStation;
import com.oc.projet5webapp.service.FireStationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/firestation")
public class FireStationController {

    private final FireStationService fireStationService;

    public FireStationController(FireStationService fireStationService) {
        this.fireStationService = fireStationService;
    }


    /**
     * Create a new fire station.
     *
     * @param fireStation the fire station to create, provided in the request body
     * @return a ResponseEntity containing the created fire station with HTTP status 200 OK
     */
    @PostMapping
    public ResponseEntity<FireStation> createFireStation(@Valid @RequestBody FireStation fireStation) {

        FireStation newFireStation = fireStationService.addFirestation(fireStation);
        return ResponseEntity.ok(newFireStation);
    }



    /**
     * Update the fire station number of an existing fire station.
     *
     * @param fireStation the fire station to update
     * @return a ResponseEntity containing the updated fire station
     *         with HTTP status 200 OK, or an empty response
     *         with HTTP status 404 Not Found if the fire station does not exist
     */
    @PutMapping
    public ResponseEntity<FireStation> updateFireStation(@Valid @RequestBody FireStation fireStation) {
        Optional<FireStation> updatedFireStation = fireStationService.updateFireStation(fireStation);
        return updatedFireStation
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    /**
     * Delete a fire station identified by their address and station number.
     *
     * @param address the address of the fire station to delete
     * @param stationNumber the station number of the fire station to delete
     * @return an empty response with HTTP status 204 No Content
     *         if the fire station was deleted, or HTTP status 404 Not Found
     *         if no matching fire station exists
     */
    @DeleteMapping("/{address}/{stationNumber}")
    public ResponseEntity<Void> deleteFireStation(@PathVariable String address, @PathVariable int stationNumber) {

        boolean deleted = fireStationService.deleteFireStation(address, stationNumber);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
