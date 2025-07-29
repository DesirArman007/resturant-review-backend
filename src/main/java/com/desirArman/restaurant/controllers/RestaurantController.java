package com.desirArman.restaurant.controllers;

import com.desirArman.restaurant.domain.RestaurantCreateUpdateRequest;
import com.desirArman.restaurant.domain.dtos.RestaurantCreateUpdateRequestDto;
import com.desirArman.restaurant.domain.dtos.RestaurantDto;
import com.desirArman.restaurant.domain.dtos.RestaurantSummaryDto;
import com.desirArman.restaurant.domain.entities.Restaurant;
import com.desirArman.restaurant.mappers.RestaurantMapper;
import com.desirArman.restaurant.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;

    @PostMapping
    public ResponseEntity<RestaurantDto> createRestaurant(@Valid @RequestBody RestaurantCreateUpdateRequestDto requestDto){

        // These debug prints can be uncommented to verify DTO mapping during development.
        //System.out.println("Incoming DTO: " + requestDto);
        //System.out.println("Mapped domain: " + request);

        RestaurantCreateUpdateRequest request = restaurantMapper.toRestaurantCreateUpdateRequest(requestDto);

        Restaurant resturant = restaurantService.createRestaurant(request);
        RestaurantDto createdRestaurantDto = restaurantMapper.toRestaurantDto(resturant);

        return ResponseEntity.ok(createdRestaurantDto);
    }

    @GetMapping(path = "/search")
    private Page<RestaurantSummaryDto> searchResturants(
             @RequestParam(required = false) String q,
             @RequestParam(required = false) Float minRating,
             @RequestParam(required = false) Float latitude,
             @RequestParam(required = false) Float longitude,
             @RequestParam(required = false) Float readius,
             @RequestParam(defaultValue = "1") int page,
             @RequestParam(defaultValue = "20") int size

    ){
        Page<Restaurant> searchResults = restaurantService.searchRestaurants(
                q,minRating,latitude,longitude,readius, PageRequest.of(page - 1, size)
        );

       return searchResults
               .map(restaurant -> restaurantMapper
                        .toSummaryDto(restaurant));

    }
}
