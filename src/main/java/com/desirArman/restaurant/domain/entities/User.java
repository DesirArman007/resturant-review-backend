package com.desirArman.restaurant.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Text)
    private String username;

    @Field(type = FieldType.Text)
    private String givenName;

    @Field(type = FieldType.Text)
    private String familyName;

    @Field(type = FieldType.Keyword)
    private String email;

    @Transient // ✅ Correct usage for Elasticsearch
    private Set<String> roles;
}
