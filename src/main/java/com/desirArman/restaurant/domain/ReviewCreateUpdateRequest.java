package com.desirArman.restaurant.domain;

import com.desirArman.restaurant.domain.dtos.PhotoDto;
import com.desirArman.restaurant.domain.dtos.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewCreateUpdateRequest {

    private Integer rating;

    private List<String> photoIds;

    private String content;


}
