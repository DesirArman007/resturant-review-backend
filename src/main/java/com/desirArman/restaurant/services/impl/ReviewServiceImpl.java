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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final RestaurantRepository restaurantRepository;

    @Override
    @PreAuthorize("hasRole('USER')")
    public Review createReview(User author, String restaurantId, ReviewCreateUpdateRequest request) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);

        boolean hasExsistingReview = restaurant.getReviews()
                .stream()
                .anyMatch(review -> review
                        .getWrittenBy().getId()
                        .equals(author.getId()));

        if (hasExsistingReview) {
            throw new ReviewNotAllowedException("User has already reviewed this restaurant");
        }

        List<Photo> photos = request.getPhotoIds().stream().map(url -> {
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
                .uploadDate(LocalDateTime.now())
                .edited(LocalDateTime.now())
                .photos(photos)
                .writtenBy(author)
                .build();

        restaurant.getReviews().add(reviewToCreate);
        updateRestaurantAverageRating(restaurant);

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        return savedRestaurant.getReviews().stream()
                .filter(r -> reviewId.equals(r.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Error retrieving created review"));
    }

    @Override
    public Page<Review> listReviews(String restaurantId, Pageable pageable) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);

        List<Review> reviews = restaurant.getReviews();

        // for sorting the reviews
        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            Sort.Order order = sort.iterator().next();
            String property = order.getProperty();
            boolean isAscending = order.getDirection().isAscending();


            // allowing user to sort thr eviews based on these two properties
            Comparator<Review> comparator = switch (property) {
                case "datePosted" -> Comparator.comparing(Review::getUploadDate);
                case "rating" -> Comparator.comparing(Review::getRating);
                default -> Comparator.comparing(Review::getUploadDate);
            };

            reviews.sort(isAscending ? comparator : comparator.reversed());
        } else {
            // if the reviews are not sorted then it will arrange the review in order of newest one first
            reviews.sort(Comparator.comparing(Review::getUploadDate).reversed());
        }


        // pagination
        int start = (int) pageable.getOffset();

        // handling empty pages
        if (start >= reviews.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, reviews.size());
        }

        int end = Math.min(start + pageable.getPageSize(), reviews.size());

        return new PageImpl<>(reviews.subList(start, end), pageable, reviews.size());
    }


    private void updateRestaurantAverageRating(Restaurant restaurant) {
        List<Review> reviews = restaurant.getReviews();
        if (reviews.isEmpty()) {
            restaurant.setAverageRating(0.0f);
        } else {
            double avgRating = reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average().orElse(0.0f);

            restaurant.setAverageRating((float) avgRating);
        }
    }

    private Restaurant getRestaurantOrThrow(String restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + restaurantId));
    }


    public Optional<Review> getReview(String restaurantId, String reviewId) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);

        return getReviewsFromRestaurant(reviewId, restaurant);
    }

    private static Optional<Review> getReviewsFromRestaurant(String reviewId, Restaurant restaurant) {
        return restaurant.getReviews()
                .stream()
                .filter((review -> reviewId.equals(review.getId())))
                .findFirst();
    }


    @Override
    @PreAuthorize("hasRole('USER')")
    public Review updateReview(User author, String restaurantId, String reviewId, ReviewCreateUpdateRequest request) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);

        String authorId = author.getId();

       Review existingReview = getReviewsFromRestaurant( reviewId, restaurant)
                .orElseThrow(() -> new ReviewNotAllowedException("Review does not exist"));

       if(!authorId.equals(existingReview.getWrittenBy().getId())){
            throw new ReviewNotAllowedException("Cannot update another users review");
       }

       if(LocalDateTime.now().isAfter(existingReview.getUploadDate().plusHours(48))){
           throw new ReviewNotAllowedException("You can only edit your review within 48 hours of posting.");
       }

       existingReview.setContent(request.getContent());
       existingReview.setRating(request.getRating());
       existingReview.setEdited(LocalDateTime.now());

       existingReview.setPhotos(request.getPhotoIds().stream().map(url ->  Photo.builder()
                   .url(url)
                   .uploadDate(LocalDateTime.now())
                   .build()).toList());


      List<Review> updatedReviews = restaurant.getReviews()
              .stream()
              .filter(review -> !reviewId.equals(review.getId()))
              .collect(Collectors.toList());

      updatedReviews.add(existingReview);

      updateRestaurantAverageRating(restaurant);

      restaurant.setReviews(updatedReviews);

      restaurantRepository.save(restaurant);

      return existingReview;
    }

    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void deleteReview(String restaurantId, String reviewId, User caller) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);

        Review existingReview = getReviewsFromRestaurant( reviewId, restaurant)
                .orElseThrow(() -> new ReviewNotAllowedException("Review does not exist"));


        boolean isOwner = existingReview.getWrittenBy().getId().equals(caller.getId());
        boolean isAdmin = caller.getRoles().contains("ADMIN");

        if (!(isOwner || isAdmin)) {
            throw new ReviewNotAllowedException("You can only delete your own reviews or be an admin.");
        }

//        if (!existingReview.getWrittenBy().getId().equals(author.getId())) {
//            throw new ReviewNotAllowedException("You can only delete your own reviews.");
//        }

        List<Review> filteredReviews = restaurant.getReviews().stream()
                .filter(r -> !reviewId.equals(r.getId()))
                .toList();

        restaurant.setReviews(filteredReviews); // Set the new list
        updateRestaurantAverageRating(restaurant); // Recalculate average
        restaurantRepository.save(restaurant); // Save changes
    }
}