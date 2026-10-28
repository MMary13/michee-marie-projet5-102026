package com.oc.projet5webapp.controller;

import com.oc.projet5webapp.model.Person;
import com.oc.projet5webapp.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonService personService;

    /**
     * Creates a controller for managing persons.
     *
     * @param personService the service responsible for person operations
     */
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Create a new person.
     *
     * @param person the person to create, provided in the request body
     * @return a ResponseEntity containing the created person
     *         with HTTP status 200 OK
     */
    @PostMapping
    public ResponseEntity<Person> createPerson(@Valid @RequestBody Person person) {

        Person newPerson = personService.addPerson(person);
        return ResponseEntity.ok(newPerson);
    }



    /**
     * Update an existing person's information.
     * The person is identified by their first name and last name.
     * These fields remain unchanged; only the other supported fields
     * are updated.
     *
     * @param person the person containing the identifying information
     *              and the updated values, provided in the request body
     * @return a ResponseEntity containing the updated person
     *         with HTTP status 200 OK, or an empty response
     *         with HTTP status 404 Not Found if the person does not exist
     */
    @PutMapping
    public ResponseEntity<Person> updatePerson(@Valid @RequestBody Person person) {
        Optional<Person> updatedPerson = personService.updatePerson(person);
        return updatedPerson
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    /**
     * Delete a person identified by their first name and last name.
     *
     * @param firstName the first name of the person to delete
     * @param lastName the last name of the person to delete
     * @return an empty response with HTTP status 204 No Content
     *         if the person was deleted, or HTTP status 404 Not Found
     *         if no matching person exists
     */
    @DeleteMapping("/{firstName}/{lastName}")
    public ResponseEntity<Void> deletePerson(@PathVariable String firstName, @PathVariable String lastName) {

        boolean deleted = personService.deletePerson(firstName, lastName);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
