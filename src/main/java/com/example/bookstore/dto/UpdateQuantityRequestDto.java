package com.example.bookstore.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateQuantityRequestDto {
    @Min(0)
    private int quantity;
}
