package com.example.bookstore.service;

import com.example.bookstore.dto.UserRegistrationRequestDto;
import com.example.bookstore.dto.UserResponseDto;
import com.example.bookstore.exception.RegistrationException;
import com.example.bookstore.model.User;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException;

    User findByEmail(String email);
}
