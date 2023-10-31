package com.example.bookstore.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateQuantityRequestDto {
    @Min(0)
    private int quantity;
}
