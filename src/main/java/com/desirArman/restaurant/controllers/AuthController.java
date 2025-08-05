package com.desirArman.restaurant.controllers;

import com.desirArman.restaurant.domain.UserCreateUpdateRequest;
import com.desirArman.restaurant.domain.dtos.UserCreateUpdateRequestDto;
import com.desirArman.restaurant.domain.dtos.UserDto;
import com.desirArman.restaurant.domain.entities.User;
import com.desirArman.restaurant.mappers.UserMapper;
import com.desirArman.restaurant.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path ="/api/auth" )
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserCreateUpdateRequestDto requestDto) {
        // 1. Map the incoming DTO to the service request object
        UserCreateUpdateRequest createRequest = userMapper.toUserCreateUpdateRequest(requestDto);

        // 2. Call the service to create the user in Keycloak
        User createdUser = userService.createUser(createRequest);

        // 3. Map the resulting User entity to a DTO for the response
        UserDto responseDto = userMapper.toDto(createdUser);

        // 4. Return a 201 Created status with the new user's data
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
