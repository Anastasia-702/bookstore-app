package com.example.bookstore.dto;

import com.example.bookstore.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@FieldMatch(
        field = "password",
        fieldMatch = "repeatPassword",
        message = "Passwords do not match!")
public class UserRegistrationRequestDto {
    @Email
    private String email;
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;
    private String repeatPassword;
    @NotBlank
    @Size(min = 2, max = 100)
    private String firstName;
    @NotBlank
    @Size(min = 2, max = 100)
    private String lastName;
    private String shippingAddress;
}
