package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfig;
import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.CreateBookRequestDto;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    Book toModel(CreateBookRequestDto dto);

    @Mapping(target = "categoryIds", ignore = true)
    BookDto toDto(Book book);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        if (book.getCategories() == null) {
            toDtoWithoutCategories(book);
        } else {
            Set<Long> categoryIds = book.getCategories().stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet());
            bookDto.setCategoryIds(categoryIds);
        }
    }

    @Named("bookFromId")
    default Book bookFromId(Long id) {
        if (id == null) {
            return null;
        }
        Book book = new Book();
        book.setId(id);
        return book;
    }
}
