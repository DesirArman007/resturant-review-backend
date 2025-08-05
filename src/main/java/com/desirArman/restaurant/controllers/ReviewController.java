package com.desirArman.restaurant.controllers;


import com.desirArman.restaurant.domain.ReviewCreateUpdateRequest;
import com.desirArman.restaurant.domain.dtos.ReviewCreateUpdateRequestDto;
import com.desirArman.restaurant.domain.dtos.ReviewDto;
import com.desirArman.restaurant.domain.entities.Review;
import com.desirArman.restaurant.domain.entities.User;
import com.desirArman.restaurant.mappers.ReviewMapper;
import com.desirArman.restaurant.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/restaurants/{restaurantId}/reviews")
public class ReviewController {

    private final ReviewMapper reviewMapper;
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(
            @PathVariable String restaurantId,
            @Valid @RequestBody ReviewCreateUpdateRequestDto requestDto,
            @AuthenticationPrincipal  Jwt jwt){

        ReviewCreateUpdateRequest reviewCreateUpdateRequest = reviewMapper.toReviewCreateUpdateRequest(requestDto);

        User user = jwtToUser(jwt);
        Review createdReview = reviewService.createReview(user, restaurantId, reviewCreateUpdateRequest);

        ReviewDto createdReviewDto = reviewMapper.toDto(createdReview);

        return ResponseEntity.ok(createdReviewDto);
    }

    @GetMapping
    public Page<ReviewDto> listReviews(@PathVariable String restaurantId,
                                       @PageableDefault(
                                               size = 20,
                                               page = 0,
                                               sort = "datePosted",
                                               direction = Sort.Direction.DESC )Pageable pageable){

      return  reviewService.listReviews(restaurantId,pageable)
              .map(review -> reviewMapper.toDto(review));

    }

    @GetMapping(path = "/{reviewId}")
    public ResponseEntity<ReviewDto> getReview(@PathVariable String restaurantId, @PathVariable String reviewId){
        return reviewService
                .getReview(restaurantId, reviewId)
                .map(review -> reviewMapper.toDto(review))
                .map(ResponseEntity::ok)
                .orElseGet(()-> ResponseEntity.noContent().build());

    }

    @PutMapping(path = "/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable String restaurantId,
            @PathVariable String reviewId,
            @Valid @RequestBody ReviewCreateUpdateRequestDto requestDto,
            @AuthenticationPrincipal  Jwt jwt
    ){
        ReviewCreateUpdateRequest request = reviewMapper.toReviewCreateUpdateRequest(requestDto);
        User user = jwtToUser(jwt);

       Review updatedReview =  reviewService.updateReview(user,restaurantId,reviewId,request);
       ReviewDto updatedReviewDto = reviewMapper.toDto(updatedReview);

       return ResponseEntity.ok(updatedReviewDto);
    }


    @DeleteMapping(path = "/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable String restaurantId, @PathVariable String reviewId,@AuthenticationPrincipal  Jwt jwt){
        User user = jwtToUser(jwt);

        reviewService.deleteReview(restaurantId,reviewId,user);

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


