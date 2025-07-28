package com.desirArman.restaurant.controllers;

import com.desirArman.restaurant.domain.RestaurantCreateUpdateRequest;
import com.desirArman.restaurant.domain.dtos.RestaurantCreateUpdateRequestDto;
import com.desirArman.restaurant.domain.dtos.RestaurantDto;
import com.desirArman.restaurant.domain.entities.Restaurant;
import com.desirArman.restaurant.mappers.RestaurantMapper;
import com.desirArman.restaurant.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
