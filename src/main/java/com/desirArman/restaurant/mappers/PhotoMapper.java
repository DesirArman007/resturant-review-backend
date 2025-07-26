package com.desirArman.restaurant.mappers;

import com.desirArman.restaurant.domain.dtos.PhotoDto;
import com.desirArman.restaurant.domain.entities.Photo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PhotoMapper {

    PhotoDto toDto(Photo photo);
}
