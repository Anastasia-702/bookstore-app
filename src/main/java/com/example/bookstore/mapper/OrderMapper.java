package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfig;
import com.example.bookstore.dto.OrderResponseDto;
import com.example.bookstore.model.Order;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    OrderResponseDto toDto(Order order);

    @AfterMapping
    default void setUserId(@MappingTarget OrderResponseDto responseDto, Order order) {
        responseDto.setUserId(order.getUser().getId());
    }
}
