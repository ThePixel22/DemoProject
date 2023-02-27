package com.example.demospring.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

@Slf4j
public class GeoUtil {
    @Value("${maps.api.key}")
    private static String mapKey;

    @Value("${map.api.geocode}")
    private static String mapUrl;

    public static String getLatLongByAddress(String address) {
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String,String> querryParams = new LinkedMultiValueMap<>();

        querryParams.put("address", Arrays.asList(address));
        querryParams.put("key", Arrays.asList(mapKey));





        return "";
    }

}
