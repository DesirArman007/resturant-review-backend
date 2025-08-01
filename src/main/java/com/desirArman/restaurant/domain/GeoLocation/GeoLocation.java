package com.desirArman.restaurant.domain.GeoLocation;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeoLocation {

    private Double latitude;

    private Double longitude;

}
