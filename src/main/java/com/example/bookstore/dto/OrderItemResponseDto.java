package com.example.bookstore.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderItemResponseDto {
    private Long id;
    private Long bookId;
    private int quantity;
}
