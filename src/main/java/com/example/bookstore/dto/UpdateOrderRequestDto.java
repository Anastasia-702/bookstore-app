package com.example.bookstore.dto;

import com.example.bookstore.model.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderRequestDto {
    private Order.Status status;
}
