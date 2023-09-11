package com.example.bookstore.dto;

import lombok.Data;

@Data
public class UserLoginResponseDto {
    private final String token;

    public UserLoginResponseDto(String token) {
        this.token = token;
    }
}
