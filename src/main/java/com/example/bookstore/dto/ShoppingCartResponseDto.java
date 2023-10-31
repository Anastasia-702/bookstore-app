package com.example.bookstore.dto;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ShoppingCartResponseDto {
    private Long id;
    private Long userId;
    private Set<CartItemResponseDto> cartItems;
}
