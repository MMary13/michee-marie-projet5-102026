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
class FireStationControllerIT {

    private final MockMvc mockMvc;

    //Constructor
    FireStationControllerIT(MockMvc mockMvc) {
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
    void should_create_fireStation() throws Exception {

        String requestBody = """
                {
                    "address" : "10 rue de Paris",
                    "station" : 3
                }
                """;

        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("10 rue de Paris"))
                .andExpect(jsonPath("$.station").value("3"));
    }

    @Test
    void should_update_existing_fireStation() throws Exception {

        String requestBody = """
                {
                    "address" : "1509 Culver St",
                    "station" : 5
                }
                """;

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("1509 Culver St"))
                .andExpect(jsonPath("$.station").value("5"));
    }

    @Test
    void should_return_notFound_when_updating_non_existing_fireStation()
            throws Exception {

        String requestBody = """
                {
                    "address" : "Unknown",
                    "station" : 5
                }
                """;

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_existing_fireStation() throws Exception {

        mockMvc.perform(delete("/firestation/1509 Culver St/3"))
                .andExpect(status().isNoContent());
    }

    @Test
    void should_return_notFound_when_deleting_non_existing_fireStation()
            throws Exception {

        mockMvc.perform(delete("/firestation/Unknown/4"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_badRequest_when_creating_fireStation_with_invalid_data()
            throws Exception {

        String requestBody = """
                {
                    "address" : "",
                    "station" : 5
                }
                """;

        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_list_of_persons_for_one_fireStation() throws Exception {

        mockMvc.perform(get("/firestation?stationNumber=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adults").value("2"))
                .andExpect(jsonPath("$.children").value("1"));
        //TODO: test le nombre d'adultes, d'enfants et la liste de personnes retournées
    }

}
