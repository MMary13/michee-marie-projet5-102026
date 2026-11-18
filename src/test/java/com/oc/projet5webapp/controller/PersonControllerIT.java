package com.oc.projet5webapp.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.data-file=src/test/resources/data-test.json"
})
@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class PersonControllerIT {

    private final MockMvc mockMvc;

    //Constructor
    PersonControllerIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    void setUp() throws IOException {
        Files.copy(
                Path.of("src/test/resources/data-test-initial.json"),
                Path.of("src/test/resources/data-test.json"),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    @Test
    void should_create_person() throws Exception {

        String requestBody = """
                {
                    "firstName": "Jean",
                    "lastName": "Dupont",
                    "address": "15 rue Victor Hugo",
                    "city": "Paris",
                    "zip": "75001",
                    "phone": "0612345678",
                    "email": "jean@example.com"
                }
                """;

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jean"))
                .andExpect(jsonPath("$.lastName").value("Dupont"))
                .andExpect(jsonPath("$.address").value("15 rue Victor Hugo"))
                .andExpect(jsonPath("$.zip").value("75001"))
                .andExpect(jsonPath("$.city").value("Paris"))
                .andExpect(jsonPath("$.phone").value("0612345678"))
                .andExpect(jsonPath("$.email").value("jean@example.com"));
    }

    @Test
    void should_update_existing_person() throws Exception {

        String requestBody = """
                {
                    "firstName": "Marie",
                    "lastName": "Michee",
                    "address": "20 rue Victor Hugo",
                    "city": "Rennes",
                    "zip": "35000",
                    "phone": "0612345678",
                    "email": "michee@example.com"
                }
                """;

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Marie"))
                .andExpect(jsonPath("$.lastName").value("Michee"))
                .andExpect(jsonPath("$.city").value("Rennes"));
    }

    @Test
    void should_return_notFound_when_updating_non_existing_person()
            throws Exception {

        String requestBody = """
                {
                    "firstName": "Unknown",
                    "lastName": "Person",
                    "address": "45 boulevard du roi",
                    "city": "Versailles",
                    "zip": "78000",
                    "phone": "0612345678",
                    "email": "unknown@example.com"
                }
                """;

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_existing_person() throws Exception {

        mockMvc.perform(delete("/person/Marie/Michee"))
                .andExpect(status().isNoContent());
    }

    @Test
    void should_return_notFound_when_deleting_non_existing_person()
            throws Exception {

        mockMvc.perform(delete("/person/Unknown/Person"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_badRequest_when_creating_person_with_invalid_data()
            throws Exception {

        String requestBody = """
                {
                    "firstName": "",
                    "lastName": "Dupont",
                    "email": "invalid-email"
                }
                """;

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
