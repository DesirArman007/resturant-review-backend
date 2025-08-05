package com.desirArman.restaurant.controllers;

import com.desirArman.restaurant.domain.RestaurantCreateUpdateRequest;
import com.desirArman.restaurant.domain.dtos.RestaurantCreateUpdateRequestDto;
import com.desirArman.restaurant.domain.dtos.RestaurantDto;
import com.desirArman.restaurant.domain.dtos.RestaurantSummaryDto;
import com.desirArman.restaurant.domain.entities.Restaurant;
import com.desirArman.restaurant.domain.entities.User;
import com.desirArman.restaurant.exceptions.EntityNotFoundException;
import com.desirArman.restaurant.mappers.RestaurantMapper;
import com.desirArman.restaurant.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;

    @PostMapping
    public ResponseEntity<RestaurantDto> createRestaurant(@Valid @RequestBody RestaurantCreateUpdateRequestDto requestDto,
                                                          @AuthenticationPrincipal Jwt jwt) {

        // These debug prints can be uncommented to verify DTO mapping during development.
        //System.out.println("Incoming DTO: " + requestDto);
        //System.out.println("Mapped domain: " + request);
        User user = jwtToUser(jwt);

        RestaurantCreateUpdateRequest request = restaurantMapper.toRestaurantCreateUpdateRequest(requestDto);

        Restaurant resturant = restaurantService.createRestaurant(user,request);
        RestaurantDto createdRestaurantDto = restaurantMapper.toRestaurantDto(resturant);

        return ResponseEntity.ok(createdRestaurantDto);
    }

    @GetMapping(path = "/search")
    public Page<RestaurantSummaryDto> searchResturants(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Float minRating,
            @RequestParam(required = false) Float latitude,
            @RequestParam(required = false) Float longitude,
            @RequestParam(required = false) Float readius,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size

    ) {
        Page<Restaurant> searchResults = restaurantService.searchRestaurants(
                q, minRating, latitude, longitude, readius, PageRequest.of(page - 1, size)
        );

        return searchResults
                .map(restaurant -> restaurantMapper
                        .toSummaryDto(restaurant));

    }

    @GetMapping(path = "/{restaurant_id}")
    public ResponseEntity<RestaurantDto> getRestaurant(@PathVariable("restaurant_id") String restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurant(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with id: " + restaurantId));
        RestaurantDto restaurantDto = restaurantMapper.toRestaurantDto(restaurant);
        return ResponseEntity.ok(restaurantDto);
    }


    @PutMapping(path = "/{restaurant_id}")
    public ResponseEntity<RestaurantDto> updateRestaurant(
            @PathVariable("restaurant_id")String restaurantId,
            @Valid @RequestBody RestaurantCreateUpdateRequestDto requestDto,
            @AuthenticationPrincipal Jwt jwt
    ){
        RestaurantCreateUpdateRequest request = restaurantMapper.toRestaurantCreateUpdateRequest(requestDto);
        User user = jwtToUser(jwt);


        Restaurant updatedRestaurant = restaurantService.updateRestaurant(restaurantId,request, user);

        RestaurantDto updatedRestaurantDto = restaurantMapper.toRestaurantDto(updatedRestaurant);

        return ResponseEntity.ok(updatedRestaurantDto);
    }


    @DeleteMapping(path = "/{restaurant_id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable("restaurant_id")String restaurantId,  @AuthenticationPrincipal Jwt jwt){

        User user = jwtToUser(jwt);
        restaurantService.deleteRestaurant(restaurantId,user);
        return ResponseEntity.noContent().build();
    }
    private User jwtToUser(Jwt jwt) {
        List<String> roleList = jwt.getClaimAsMap("realm_access") != null
                ? (List<String>) ((Map<String, Object>) jwt.getClaim("realm_access")).get("roles")
                : List.of();

        return User.builder()
                .id(jwt.getSubject())
                .username(jwt.getClaimAsString("preferred_username"))
                .givenName(jwt.getClaimAsString("given_name"))
                .familyName(jwt.getClaimAsString("family_name"))
                .roles(new HashSet<>(roleList))
                .build();
    }



}
