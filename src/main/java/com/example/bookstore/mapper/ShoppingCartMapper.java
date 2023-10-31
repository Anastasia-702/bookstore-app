package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfig;
import com.example.bookstore.dto.ShoppingCartResponseDto;
import com.example.bookstore.model.ShoppingCart;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    ShoppingCartResponseDto toDto(ShoppingCart cart);

    @AfterMapping
    default void setUserId(@MappingTarget ShoppingCartResponseDto responseDto, ShoppingCart cart) {
        responseDto.setUserId(cart.getUser().getId());
    }
}
