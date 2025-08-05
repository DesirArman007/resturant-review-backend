package com.desirArman.restaurant.services;

import com.desirArman.restaurant.domain.UserCreateUpdateRequest;
import com.desirArman.restaurant.domain.dtos.UserDto;
import com.desirArman.restaurant.domain.entities.User;

public interface UserService {

    User createUser(UserCreateUpdateRequest request);

}
