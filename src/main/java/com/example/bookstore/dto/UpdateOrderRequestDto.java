package com.example.bookstore.dto;

import com.example.bookstore.model.Order;
import lombok.Data;

@Data
public class UpdateOrderRequestDto {
    private Order.Status status;
}
