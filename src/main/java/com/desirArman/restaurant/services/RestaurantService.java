package com.desirArman.restaurant.services;

import com.desirArman.restaurant.domain.RestaurantCreateUpdateRequest;
import com.desirArman.restaurant.domain.entities.Restaurant;
import com.desirArman.restaurant.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RestaurantService {
    Restaurant createRestaurant(User owner, RestaurantCreateUpdateRequest request);

    Page<Restaurant> searchRestaurants(
            String query,
            Float minRating,
            Float latitude,
            Float longitude,
            Float radius,
            Pageable pageable
    );

    Optional<Restaurant> getRestaurant(String id);

    Restaurant updateRestaurant(String id, RestaurantCreateUpdateRequest restaurantCreateUpdateRequest, User owner);

    void deleteRestaurant(String id, User owner);
}
