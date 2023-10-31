package com.example.bookstore.service;

import com.example.bookstore.dto.CreateOrderRequestDto;
import com.example.bookstore.dto.OrderItemResponseDto;
import com.example.bookstore.dto.OrderResponseDto;
import com.example.bookstore.dto.UpdateOrderRequestDto;
import com.example.bookstore.model.User;
import java.util.List;
import java.util.Set;

public interface OrderService {
    OrderResponseDto createOrder(User user, CreateOrderRequestDto requestDto);

    List<OrderResponseDto> getAllOrders(User user);

    void updateStatus(Long orderId, UpdateOrderRequestDto requestDto);

    Set<OrderItemResponseDto> getAllItems(User user, Long orderId);

    OrderItemResponseDto getItem(User user, Long orderId, Long itemId);
}
