package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfig;
import com.example.bookstore.dto.CartItemRequestDto;
import com.example.bookstore.dto.CartItemResponseDto;
import com.example.bookstore.model.CartItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = BookMapper.class)
public interface CartItemMapper {
    @Mapping(source = "bookId", target = "book",
            qualifiedByName = "com.example.BookMapper.bookFromId")
    CartItem toModel(CartItemRequestDto requestDto);

    CartItemResponseDto toDto(CartItem cartItem);

    @AfterMapping
    default void setBookIdAndTitle(@MappingTarget CartItemResponseDto responseDto,
                                   CartItem cartItem) {
        responseDto.setBookId(cartItem.getBook().getId());
        responseDto.setBookTitle(cartItem.getBook().getTitle());
    }
}
