
package com.desirArman.restaurant.services.impl;

import com.desirArman.restaurant.domain.GeoLocation.GeoLocation;
import com.desirArman.restaurant.domain.RestaurantCreateUpdateRequest;
import com.desirArman.restaurant.domain.entities.*;
import com.desirArman.restaurant.exceptions.EntityNotFoundException;
import com.desirArman.restaurant.repositories.RestaurantRepository;
import com.desirArman.restaurant.services.GeoLocationService;
import com.desirArman.restaurant.services.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final GeoLocationService geoLocationService;

    @Override
    @PreAuthorize("hasRole('OWNER')")
    public Restaurant createRestaurant(User owner, RestaurantCreateUpdateRequest request) {
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
                 .createdBy(owner)
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
            return  restaurantRepository.findByQueryAndMinRating(query, searchMinRating, pageable);
        }

        if(null != longitude && null != latitude && null != radius){
            return restaurantRepository.findByLocationNear(latitude, longitude, radius, pageable);
        }

        // if every single argument is null so we return all restuarnts
        return  restaurantRepository.findAll(pageable);
    }

    @Override
    public Optional<Restaurant> getRestaurant(String id) {
       return restaurantRepository.findById(id);

    }

    @Override
    @PreAuthorize("hasRole('OWNER')")
    public Restaurant updateRestaurant(String id, RestaurantCreateUpdateRequest restaurantCreateUpdateRequest, User owner) {
        Restaurant restaurant = getRestaurant(id)
                .orElseThrow(()-> new EntityNotFoundException("Restaurant not found with id: "+id));

        if (!restaurant.getCreatedBy().getId().equals(owner.getId())) {
            throw new AccessDeniedException("You can only modify your own restaurants.");
        }


        GeoLocation newGeoLocation =geoLocationService.getLocation(restaurantCreateUpdateRequest.getAddress());

       GeoPoint newGeoPoint = new GeoPoint(newGeoLocation.getLatitude(), newGeoLocation.getLongitude());

        List<String> photoIds= restaurantCreateUpdateRequest.getPhotoIds();
        List<Photo> photos= photoIds.stream().map(photoUrl -> Photo.builder()
                .url(photoUrl)
                .uploadDate(LocalDateTime.now())
                .build()).toList();

        restaurant.setName(restaurantCreateUpdateRequest.getName());
        restaurant.setCuisineType(restaurantCreateUpdateRequest.getCuisineType());
        restaurant.setContactInformation(restaurantCreateUpdateRequest.getContactInformation());
        restaurant.setAddress(restaurantCreateUpdateRequest.getAddress());
        restaurant.setGeoLocation(newGeoPoint);
        restaurant.setOperatingHours(restaurantCreateUpdateRequest.getOperatingHours());
        restaurant.setPhotos(photos);

        return  restaurantRepository.save(restaurant);

    }

    @Override
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public void deleteRestaurant(String id, User caller) {
        Restaurant restaurant = getRestaurant(id)
                .orElseThrow(()-> new EntityNotFoundException("Restaurant not found with id: "+id));

        boolean isOwner = restaurant.getCreatedBy().getId().equals(caller.getId());
        boolean isAdmin = caller.getRoles().contains("ADMIN");

        if (!(isOwner || isAdmin)) {
            throw new AccessDeniedException("You can only delete your own restaurants or be an admin.");
        }

//        if (!restaurant.getCreatedBy().getId().equals(caller.getId())) {
//            throw new AccessDeniedException("You can only delete your own restaurants.");
//        }


        restaurantRepository.deleteById(id);
    }
}
