package com.oc.projet5webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FireStationResponse {

    private List<FireStationPerson> persons;
    private int adultCount;
    private int childCount;

}
