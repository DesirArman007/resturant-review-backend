package com.desirArman.restaurant.domain;


import com.desirArman.restaurant.domain.entities.Address;
import com.desirArman.restaurant.domain.entities.OperatingHours;
import com.desirArman.restaurant.domain.entities.Photo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantCreatUpdateRequest {

    private String name;

    private String cuisineType;

    private String contactInformation;

    private Address address;

    private OperatingHours operatingHours;

    private List<String> photoIds;

}
