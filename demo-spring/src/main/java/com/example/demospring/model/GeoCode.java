package com.example.demospring.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Jacksonized
@Builder
public class GeoCode {

    @JsonProperty("results")
    private List<GeoInfo> results;

}
