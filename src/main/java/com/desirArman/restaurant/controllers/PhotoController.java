package com.desirArman.restaurant.controllers;

import com.desirArman.restaurant.domain.dtos.PhotoDto;
import com.desirArman.restaurant.domain.entities.Photo;
import com.desirArman.restaurant.mappers.PhotoMapper;
import com.desirArman.restaurant.services.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/photos")
public class PhotoController {

    private final PhotoService photoService;

    private final PhotoMapper photoMapper;


    @PostMapping
    public PhotoDto uploadPhoto(@RequestParam("file")MultipartFile file){
        Photo savedPhoto = photoService.uploadPhoto(file);
        return photoMapper.toDto(savedPhoto);
    }

    @GetMapping(path = "/{id:.+}" )
    public ResponseEntity<Resource> getPhoto(@PathVariable String id){
    return  photoService.getPhotoAsResource(id).map(photo ->
                 ResponseEntity.ok()
                         .contentType(
                                 MediaTypeFactory.getMediaType(photo)
                                         .orElse(MediaType.APPLICATION_OCTET_STREAM)
                         )
                         .header(HttpHeaders.CONTENT_DISPOSITION,"inline")
                         .body(photo)
         ).orElse(ResponseEntity.notFound().build());
    }

}
