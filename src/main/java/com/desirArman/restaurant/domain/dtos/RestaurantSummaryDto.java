package com.desirArman.restaurant.domain.dtos;

import com.desirArman.restaurant.domain.entities.Photo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantSummaryDto {

    private String id;
    private String name;
    private String CuisineType;
    private Float averageRating;
    private Integer totalReviews;
    private AddressDto address;
    private List<Photo> photos;
}
