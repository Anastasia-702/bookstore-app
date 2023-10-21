package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfig;
import com.example.bookstore.dto.CategoryRequestDto;
import com.example.bookstore.dto.CategoryResponseDto;
import com.example.bookstore.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    Category toModel(CategoryRequestDto requestDto);

    CategoryResponseDto toDto(Category category);
}
