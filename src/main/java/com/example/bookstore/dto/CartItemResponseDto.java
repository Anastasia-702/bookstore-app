package com.example.bookstore.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartItemResponseDto {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private int quantity;
}
