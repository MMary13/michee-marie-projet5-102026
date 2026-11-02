package com.oc.projet5webapp.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FireStation {

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Station is required")
    private int station;
}
