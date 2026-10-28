package com.oc.projet5webapp.service;

import com.oc.projet5webapp.model.Person;
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
public class PersonService {

    private final ObjectMapper objectMapper;
    private final Path dataFile = Path.of("data.json");

    public PersonService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Adds a person to the JSON data file.
     * Existing persons and other JSON properties are preserved.
     *
     * @param person the person to add
     * @return the added person
     * @throws NullPointerException if person is null
     */
    public Person addPerson(@Nonnull Person person) {
        //Validate person
        Objects.requireNonNull(person, "Person must not be null");

        // read file
        List<Person> persons = readPersons();

        // add the new person
        persons.add(person);

        // Update file
        updatePersonsNodeInFile(persons);

        return person;
    }

    /**
     * Updates an existing person's address, city, zip code and phone number.
     * The first name and last name are used to identify the person
     * and remain unchanged.
     *
     * @param person the person containing the updated information
     * @return an Optional containing the updated person,
     *         or an empty Optional if no matching person exists
     * @throws NullPointerException if person is null
     */
    public Optional<Person> updatePerson(@Nonnull Person person) {
        //Validate person
        Objects.requireNonNull(person, "Person must not be null");
        // Read file
        List<Person> persons = readPersons();

        // Search for the person
        Optional<Person> existingPerson = findPerson(persons, person.getFirstName(), person.getLastName());

        if (existingPerson.isEmpty()) {
            return Optional.empty();
        }
        // Update the person in the list
        Person personToUpdate = existingPerson.get();
        personToUpdate.setAddress(person.getAddress());
        personToUpdate.setCity(person.getCity());
        personToUpdate.setZip(person.getZip());
        personToUpdate.setPhone(person.getPhone());

        //Update file
        updatePersonsNodeInFile(persons);

        return Optional.of(personToUpdate);
    }


    /**
     * Deletes a person identified by their first name and last name.
     *
     * @param firstName the first name of the person to delete
     * @param lastName the last name of the person to delete
     * @return true if a person was deleted, false if no matching person exists
     */
    public boolean deletePerson(String firstName, String lastName) {
        // Read file
        List<Person> persons = readPersons();

        // Search for the person
        Optional<Person> existingPerson = findPerson(persons, firstName, lastName);

        // Return false if the person to be deleted isn't found in the file
        if (existingPerson.isEmpty()) {
            return false;
        }
        // Remove the person
        persons.remove(existingPerson.get());
        //Update file
        updatePersonsNodeInFile(persons);
        return true;
    }


    private Optional<Person> findPerson(
            List<Person> persons,
            String firstName,
            String lastName) {

        return persons.stream()
                .filter(p -> Objects.equals(
                        p.getFirstName(), firstName)
                        && Objects.equals(
                        p.getLastName(), lastName))
                .findFirst();
    }


    private List<Person> readPersons() {
        List<Person> persons;

        // read the file and get the persons node
        JsonNode rootJson = objectMapper.readTree(dataFile.toFile());
        JsonNode personsNode = rootJson.get("persons");
        if (personsNode == null || !personsNode.isArray()) {
            return new ArrayList<>();
        }

        // convert to java objects
        persons = objectMapper.convertValue(
                personsNode,
                new TypeReference<>() {
                }
        );
        return persons;
    }

    private void updatePersonsNodeInFile(List<Person> persons) {
        File file = dataFile.toFile();
        JsonNode root = objectMapper.readTree(file);
        ObjectNode rootObject = (ObjectNode) root;
        rootObject.set("persons", objectMapper.valueToTree(persons));

        // write in file
        objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValue(file, root);

    }

}
