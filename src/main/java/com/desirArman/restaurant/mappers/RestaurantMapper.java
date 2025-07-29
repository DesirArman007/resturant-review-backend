package com.desirArman.restaurant.mappers;

import com.desirArman.restaurant.domain.GeoLocation.GeoLocation;
import com.desirArman.restaurant.domain.RestaurantCreateUpdateRequest;
import com.desirArman.restaurant.domain.dtos.GeoPointDto;
import com.desirArman.restaurant.domain.dtos.RestaurantCreateUpdateRequestDto;
import com.desirArman.restaurant.domain.dtos.RestaurantDto;
import com.desirArman.restaurant.domain.dtos.RestaurantSummaryDto;
import com.desirArman.restaurant.domain.entities.Restaurant;
import com.desirArman.restaurant.domain.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RestaurantMapper {

    RestaurantCreateUpdateRequest toRestaurantCreateUpdateRequest(RestaurantCreateUpdateRequestDto dto);

    RestaurantDto toRestaurantDto(Restaurant restaurant);

    @Mapping(source = "reviews", target = "totalReviews", qualifiedByName = "populateTotalReviews")
    RestaurantSummaryDto toSummaryDto(Restaurant restaurant);

    @Named("populateTotalReviews")
    private Integer populateTotalReviews(List<Review> reviews){
        return  reviews.size();
    }

//    @Mapping(target = "latitude", expression = "java(geoPoint.getLat())")
//    @Mapping(target = "longitude", expression = "java(geoPoint.getLon())")
    GeoPointDto toGeoPointDto(GeoPoint geoPoint);
    GeoLocation toGeoLocation(GeoPointDto dto);

}
