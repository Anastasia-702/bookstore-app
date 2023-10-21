package com.example.bookstore.service;

import com.example.bookstore.dto.CartItemRequestDto;
import com.example.bookstore.dto.ShoppingCartResponseDto;
import com.example.bookstore.dto.UpdateQuantityRequestDto;
import com.example.bookstore.model.User;

public interface ShoppingCartService {
    void addToCart(User user, CartItemRequestDto cartItemRequestDto);

    void registerNewShoppingCart(User user);

    ShoppingCartResponseDto getByUserId(Long id);

    void updateQuantity(User user, Long cartItemId, UpdateQuantityRequestDto quantity);

    void delete(User user, Long cartItemId);
}
