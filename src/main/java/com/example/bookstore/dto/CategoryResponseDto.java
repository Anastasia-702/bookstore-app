package com.example.bookstore.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryResponseDto {
    private Long id;
    private String name;
    private String description;
}
