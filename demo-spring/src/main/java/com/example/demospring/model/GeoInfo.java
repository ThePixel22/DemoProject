package com.example.demospring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
public class GeoInfo {
    @JsonProperty("geometry")
    Geometry geometry;
}
