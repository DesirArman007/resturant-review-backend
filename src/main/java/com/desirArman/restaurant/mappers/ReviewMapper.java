package com.desirArman.restaurant.mappers;

import com.desirArman.restaurant.domain.ReviewCreateUpdateRequest;
import com.desirArman.restaurant.domain.dtos.RestaurantCreateUpdateRequestDto;
import com.desirArman.restaurant.domain.dtos.ReviewCreateUpdateRequestDto;
import com.desirArman.restaurant.domain.dtos.ReviewDto;
import com.desirArman.restaurant.domain.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    ReviewCreateUpdateRequest toReviewCreateUpdateRequest(ReviewCreateUpdateRequestDto requestDto);

    @Mapping(source = "edited", target = "lastEdited")
    @Mapping(source = "uploadDate", target = "datePosted")
    ReviewDto toDto(Review review);

}
