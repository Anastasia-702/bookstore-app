package com.example.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderRequestDto {
    @NotBlank
    private String shippingAddress;
}
