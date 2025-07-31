package com.desirArman.restaurant.services;

import com.desirArman.restaurant.domain.ReviewCreateUpdateRequest;
import com.desirArman.restaurant.domain.entities.Review;
import com.desirArman.restaurant.domain.entities.User;

public interface ReviewService {

    Review createReview(User author, String restaurantId, ReviewCreateUpdateRequest request);
}
