package com.example.bookstore.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequestDto {
    @Min(1)
    private Long bookId;
    @Min(1)
    private int quantity;
}
