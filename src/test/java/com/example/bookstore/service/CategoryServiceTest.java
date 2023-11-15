package com.example.bookstore.service;

import com.example.bookstore.dto.CategoryRequestDto;
import com.example.bookstore.dto.CategoryResponseDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.CategoryMapper;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.CategoryRepository;
import com.example.bookstore.service.impl.CategoryServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("Verify get category by id works")
    void getById_ValidId_ReturnsCategoryDto() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Fiction");

        CategoryResponseDto expected = new CategoryResponseDto();
        expected.setId(category.getId());
        expected.setName(category.getName());

        Mockito.when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.of(category));
        Mockito.when(categoryMapper.toDto(category)).thenReturn(expected);

        // When
        CategoryResponseDto actual = categoryService.getById(category.getId());

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(categoryRepository, Mockito.times(1))
                .findById(category.getId());
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify get category by id with invalid id throws exception")
    void getById_InvalidId_ThrowsEntityNotFoundException() {
        // Given
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Throwable exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.getById(1L);
        }, "EntityNotFoundException was expected");

        // Then
        Assertions.assertEquals("Category with id 1 not found", exception.getLocalizedMessage());
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("Verify save category works")
    void save_ValidRequestDto_ReturnsResponseDto() {
        // Given
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Fiction");
        requestDto.setDescription("Fiction books");

        Category category = new Category();
        category.setId(1L);
        category.setName(requestDto.getName());
        category.setDescription(requestDto.getDescription());

        CategoryResponseDto expected = new CategoryResponseDto();
        expected.setId(category.getId());
        expected.setName(category.getName());
        expected.setDescription(category.getDescription());

        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(expected);
        Mockito.when(categoryMapper.toModel(requestDto)).thenReturn(category);

        // When
        CategoryResponseDto actual = categoryService.save(requestDto);

        // Then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify update category by id works")
    void update_ValidCategoryId_ReturnsResponseDto() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Fiction");

        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Fiction");
        requestDto.setDescription("Fiction books");

        CategoryResponseDto expected = new CategoryResponseDto();
        expected.setId(category.getId());
        expected.setName(requestDto.getName());
        expected.setDescription(requestDto.getDescription());

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Mockito.when(categoryMapper.toModel(requestDto)).thenReturn(category);
        Mockito.when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.of(category));
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(expected);

        // When
        CategoryResponseDto actual = categoryService.update(category.getId(), requestDto);

        // Then
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
        Mockito.verify(categoryRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(categoryRepository, Mockito.times(1))
                .findById(Mockito.any());
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify update category by id with invalid id throws exception")
    void update_InvalidCategoryId_ThrowsEntityNotFoundException() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Fiction");

        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Fiction");
        requestDto.setDescription("Fiction books");

        Mockito.when(categoryRepository.findById(category.getId())).thenReturn(Optional.empty());

        // When
        Throwable exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            categoryService.update(1L, requestDto);
        }, "EntityNotFoundException was expected");

        // Then
        Assertions.assertEquals("Can't update the category. Category with id 1 not found.",
                exception.getLocalizedMessage());
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("Verify delete category by id works")
    void deleteById_ValidId() {
        // When
        categoryService.deleteById(1L);
        // Then
        Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(1L);
    }
}
