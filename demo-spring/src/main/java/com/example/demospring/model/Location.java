package com.example.demospring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Data
public class Location {

    @JsonProperty("lat")
    private double lat;

    @JsonProperty("lng")
    private double lng;

}