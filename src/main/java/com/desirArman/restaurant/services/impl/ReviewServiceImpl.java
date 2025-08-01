package com.desirArman.restaurant.services.impl;

import com.desirArman.restaurant.domain.ReviewCreateUpdateRequest;
import com.desirArman.restaurant.domain.dtos.ReviewDto;
import com.desirArman.restaurant.domain.entities.Photo;
import com.desirArman.restaurant.domain.entities.Restaurant;
import com.desirArman.restaurant.domain.entities.Review;
import com.desirArman.restaurant.domain.entities.User;
import com.desirArman.restaurant.exceptions.EntityNotFoundException;
import com.desirArman.restaurant.exceptions.ReviewNotAllowedException;
import com.desirArman.restaurant.repositories.RestaurantRepository;
import com.desirArman.restaurant.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public Review createReview(User author, String restaurantId, ReviewCreateUpdateRequest request) {
       Restaurant restaurant= getRestaurantOrThrow(restaurantId);

       boolean hasExsistingReview =  restaurant.getReviews()
               .stream()
               .anyMatch(review -> review
                       .getWrittenBy().getId()
                       .equals(author.getId()));

       if (hasExsistingReview){
            throw new ReviewNotAllowedException("User has already reviewed this restaurant");
       }

        List<Photo> photos = request.getPhotoIds().stream().map(url ->{
            return Photo.builder()
                    .url(url)
                    .uploadDate(LocalDateTime.now())
                    .build();
        }).toList();

        String reviewId = UUID.randomUUID().toString();

        Review reviewToCreate = Review.builder()
                .id(reviewId)
                .content(request.getContent())
                .rating(request.getRating())
                .uploadDate( LocalDateTime.now())
                .edited(LocalDateTime.now())
                .photos(photos)
                .writtenBy(author)
                .build();

        restaurant.getReviews().add(reviewToCreate);
        updateRestaurantAverageRating(restaurant);

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        return savedRestaurant.getReviews().stream()
                .filter(r-> reviewId.equals(r.getId()))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Error retrieving created review"));
    }

    private Restaurant getRestaurantOrThrow(String restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + restaurantId));
    }

    private void updateRestaurantAverageRating(Restaurant restaurant){
        List<Review> reviews = restaurant.getReviews();
        if(reviews.isEmpty()){
            restaurant.setAverageRating(0.0f);
        } else{
           double avgRating =  reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average().orElse(0.0f);

           restaurant.setAverageRating((float) avgRating);
        }
    }
}
