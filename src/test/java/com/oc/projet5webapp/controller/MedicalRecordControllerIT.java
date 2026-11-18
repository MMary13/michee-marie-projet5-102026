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
class MedicalRecordControllerIT {

    private final MockMvc mockMvc;

    //Constructor
    MedicalRecordControllerIT(MockMvc mockMvc) {
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
    void should_create_medicalRecord() throws Exception {

        String requestBody = """
                {
                     "firstName" : "Marie",
                     "lastName" : "Michee",
                     "birthdate" : "03/06/1992",
                     "medications" : [ "aznol:350mg", "hydrapermazol:100mg" ],
                     "allergies" : [ "nillacilan" ]
                }
                """;

        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Marie"))
                .andExpect(jsonPath("$.lastName").value("Michee"))
                .andExpect(jsonPath("$.birthdate").value("03/06/1992"));
        //TODO: ajouter verif sur les tableaux
    }

    @Test
    void should_update_existing_medicalRecord() throws Exception {

        String requestBody = """
                 {
                     "firstName" : "Marie",
                     "lastName" : "Michee",
                     "birthdate" : "03/06/1994",
                     "medications" : [ "aznol:400mg", "hydrapermazol:150mg" ],
                     "allergies" : [ "nillacilan" ]
                }
                """;

        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Marie"))
                .andExpect(jsonPath("$.lastName").value("Michee"))
                .andExpect(jsonPath("$.birthdate").value("03/06/1994"));
        //TODO: ajouter verif sur les tableaux
    }

    @Test
    void should_return_notFound_when_updating_non_existing_medicalRecord()
            throws Exception {

        String requestBody = """
                 {
                     "firstName" : "Unknown",
                     "lastName" : "Person",
                     "birthdate" : "03/06/1994",
                     "medications" : [ "aznol:400mg", "hydrapermazol:150mg" ],
                     "allergies" : [ "nillacilan" ]
                }
                """;

        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_existing_medicalRecord() throws Exception {

        mockMvc.perform(delete("/medicalRecord/Marie/Michee"))
                .andExpect(status().isNoContent());
    }

    @Test
    void should_return_notFound_when_deleting_non_existing_medicalRecord()
            throws Exception {

        mockMvc.perform(delete("/medicalRecord/Unknown/Person"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_badRequest_when_creating_medicalRecord_with_invalid_data()
            throws Exception {

        String requestBody = """
                {
                     "firstName" : "Marie",
                     "lastName" : "Michee",
                     "birthdate" : "06-03-92",
                     "medications" : [ "aznol:400mg", "hydrapermazol:150mg" ],
                     "allergies" : [ "nillacilan" ]
                }
                """;

        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
