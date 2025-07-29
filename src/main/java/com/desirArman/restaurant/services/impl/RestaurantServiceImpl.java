package com.desirArman.restaurant.services.impl;

import com.desirArman.restaurant.domain.GeoLocation.GeoLocation;
import com.desirArman.restaurant.domain.RestaurantCreateUpdateRequest;
import com.desirArman.restaurant.domain.entities.Address;
import com.desirArman.restaurant.domain.entities.OperatingHours;
import com.desirArman.restaurant.domain.entities.Photo;
import com.desirArman.restaurant.domain.entities.Restaurant;
import com.desirArman.restaurant.repositories.RestaurantRepository;
import com.desirArman.restaurant.services.GeoLocationService;
import com.desirArman.restaurant.services.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final GeoLocationService geoLocationService;

    @Override
    public Restaurant createRestaurant(RestaurantCreateUpdateRequest request) {
        Address address = request.getAddress();
        GeoLocation geoLocation = geoLocationService.getLocation(address);
        GeoPoint geoPoint = new GeoPoint(geoLocation.getLatitude(),geoLocation.getLongitude());
         List<String> photoIds= request.getPhotoIds();
         List<Photo> photos= photoIds.stream().map(photoUrl -> Photo.builder()
                 .url(photoUrl)
                 .uploadDate(LocalDateTime.now())
                 .build()).toList();

        Restaurant restaurant = Restaurant.builder()
                 .id(UUID.randomUUID().toString())
                 .name(request.getName())
                 .cuisineType(request.getCuisineType())
                 .contactInformation(request.getContactInformation())
                 .address(address)
                 .geoLocation(geoPoint)
                 .operatingHours(request.getOperatingHours())
                 .averageRating(0f)
                 .photos(photos)
                 .build();

       return restaurantRepository.save(restaurant);
    }


    @Override
    public Page<Restaurant> searchRestaurants(
            String query, Float minRating, Float latitude,
            Float longitude, Float radius, Pageable pageable) {

        if(null != minRating && (null == query || query.isEmpty())){
         return   restaurantRepository.findByAverageRatingGreaterThanEqual(minRating, pageable);
        }

        Float searchMinRating = null == minRating ? 0f : 1;

        if( null != query && !query.trim().isEmpty()){
            return  restaurantRepository.findByQueryAndMinRating(query, minRating, pageable);
        }

        if(null != longitude && null != latitude && null != radius){
            return restaurantRepository.findByLocationNear(latitude, longitude, radius, pageable);
        }

        // if every single argument is null so we return all restuarnts
        return  restaurantRepository.findAll(pageable);
    }
}
