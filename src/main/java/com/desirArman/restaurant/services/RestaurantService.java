package com.desirArman.restaurant.services;

import com.desirArman.restaurant.domain.RestaurantCreateUpdateRequest;
import com.desirArman.restaurant.domain.entities.Restaurant;

public interface RestaurantService {
    Restaurant createRestaurant(RestaurantCreateUpdateRequest request);
}
