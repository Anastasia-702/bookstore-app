package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfig;
import com.example.bookstore.dto.OrderItemResponseDto;
import com.example.bookstore.model.OrderItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    OrderItemResponseDto toDto(OrderItem item);

    @AfterMapping
    default void setBookId(@MappingTarget OrderItemResponseDto responseDto, OrderItem item) {
        responseDto.setBookId(item.getBook().getId());
    }
}
