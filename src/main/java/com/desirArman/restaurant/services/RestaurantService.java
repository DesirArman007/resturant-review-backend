package com.desirArman.restaurant.services;

import com.desirArman.restaurant.domain.RestaurantCreatUpdateRequest;
import com.desirArman.restaurant.domain.entities.Restaurant;

public interface RestaurantService {
    Restaurant createRestaurant(RestaurantCreatUpdateRequest request);
}
