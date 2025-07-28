package com.desirArman.restaurant.services.impl;

import com.desirArman.restaurant.domain.GeoLocation.GeoLocation;
import com.desirArman.restaurant.domain.entities.Address;
import com.desirArman.restaurant.services.GeoLocationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class RandomIndiaGeoLocationService implements GeoLocationService {

    private static final double MIN_LATITUDE = 6.55;
    private static final double MAX_LATITUDE = 37.1;
    private static final double MIN_LONGITUDE = 68.1;
    private static final double MAX_LONGITUDE = 97.4;

    @Value("${spring.geolocation.nominatim.url}")
    private String nominatimUrl;

    @Override
    public GeoLocation getLocation(Address address) {
        try {
            String query = address.toQueryString();

            String url = UriComponentsBuilder.fromHttpUrl(nominatimUrl)
                    .queryParam("q", query)
                    .queryParam("format", "json")
                    .queryParam("limit", 1)
                    .build()
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "springboot-app");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = new RestTemplate().exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            if (root.isArray() && root.size() > 0) {
                JsonNode obj = root.get(0);
                double latitude = Double.parseDouble(obj.get("lat").asText());
                double longitude = Double.parseDouble(obj.get("lon").asText());
                return new GeoLocation(latitude, longitude);
            }

            log.warn("No location found for address: {}", query);

        } catch (Exception e) {
            log.error("Error while fetching geolocation. Falling back to random.", e);
        }

        // Fallback to random Indian coordinates
        Random random = new Random();
        double latitude = MIN_LATITUDE + random.nextDouble() * (MAX_LATITUDE - MIN_LATITUDE);
        double longitude = MIN_LONGITUDE + random.nextDouble() * (MAX_LONGITUDE - MIN_LONGITUDE);

        return new GeoLocation(latitude, longitude);
    }

}
