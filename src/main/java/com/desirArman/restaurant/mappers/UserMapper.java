package com.desirArman.restaurant.mappers;

import com.desirArman.restaurant.domain.UserCreateUpdateRequest;
import com.desirArman.restaurant.domain.dtos.UserCreateUpdateRequestDto;
import com.desirArman.restaurant.domain.dtos.UserDto;
import com.desirArman.restaurant.domain.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mappings({
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "firstName", target = "firstName"),
            @Mapping(source = "lastName", target = "lastName"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password")
    })
    UserCreateUpdateRequest toUserCreateUpdateRequest(UserCreateUpdateRequestDto requestDto);
    UserDto toDto(User user);

}
