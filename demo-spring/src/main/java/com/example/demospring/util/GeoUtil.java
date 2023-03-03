package com.example.demospring.util;

import com.example.demospring.model.GeoCode;
import com.example.demospring.model.Location;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class GeoUtil {
    @Value("${maps.api.key}")
    private static String mapKey = "AIzaSyAuo2i5GN-WmTIVEA1Ymhda2JkRD66uTzw";

    @Value("${map.api.geocode}")
    private static String mapUrl = "https://maps.googleapis.com/maps/api/geocode/json";

    public static Location getLatLongByAddress(String address) {
        try {
            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, String> querryParams = new LinkedMultiValueMap<>();

            querryParams.put("address", Arrays.asList(address));
            querryParams.put("key", Arrays.asList(mapKey));
            ApiClient apiClient = new ApiClient(mapUrl);

            final String[] accepts = {
                    "application/json"
            };
            final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
            final String[] contentTypes = {
                    "application/json"
            };
            final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);
            ParameterizedTypeReference<GeoCode> returnType = new ParameterizedTypeReference<GeoCode>() {
            };

            GeoCode geoCode = apiClient.invokeAPI(StringUtils.EMPTY, HttpMethod.GET, querryParams, null, null, headers, null, accept, contentType, null, returnType, true);

            if (geoCode != null && !CollectionUtils.isEmpty(geoCode.getResults())) {
                Location location = geoCode.getResults().get(0).getGeometry().getLocation();

                BigDecimal bLat = new BigDecimal(location.getLat()).setScale(8, RoundingMode.HALF_UP);
                BigDecimal bLong = new BigDecimal(location.getLng()).setScale(8, RoundingMode.HALF_UP);
                location.setLat(bLat.doubleValue());
                location.setLng(bLong.doubleValue());

                return location;
            }
        } catch (Exception e) {
            log.error("Error: ", e);
            return null;
        }


        return null;
    }

}
