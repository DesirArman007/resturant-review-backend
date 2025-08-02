package com.desirArman.restaurant.services;

import com.desirArman.restaurant.domain.ReviewCreateUpdateRequest;
import com.desirArman.restaurant.domain.entities.Review;
import com.desirArman.restaurant.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ReviewService {

    Review createReview(User author, String restaurantId, ReviewCreateUpdateRequest request);

    Page<Review> listReviews(String restaurantId, Pageable pageable);

    Optional<Review> getReview(String restaurantIds, String reviewId);

    Review updateReview(User author,String restaurantIds, String reviewId, ReviewCreateUpdateRequest request);

}
