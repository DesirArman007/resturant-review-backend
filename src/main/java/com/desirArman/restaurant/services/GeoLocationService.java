package com.desirArman.restaurant.services;

import com.desirArman.restaurant.domain.GeoLocation.GeoLocation;
import com.desirArman.restaurant.domain.entities.Address;

public interface GeoLocationService {

    GeoLocation getLocation(Address address);
}
