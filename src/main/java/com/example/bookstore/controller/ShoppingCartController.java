package com.example.bookstore.controller;

import com.example.bookstore.dto.CartItemRequestDto;
import com.example.bookstore.dto.ShoppingCartResponseDto;
import com.example.bookstore.dto.UpdateQuantityRequestDto;
import com.example.bookstore.model.User;
import com.example.bookstore.service.ShoppingCartService;
import com.example.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping cart")
public class ShoppingCartController {
    private final UserService userService;
    private final ShoppingCartService cartService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    @Operation(summary = "Add new cart item to shopping cart")
    public void addToCart(Authentication auth, @RequestBody @Valid CartItemRequestDto requestDto) {
        cartService.addToCart(getUser(auth), requestDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    @Operation(summary = "Get all items from the shopping cart")
    public ShoppingCartResponseDto get(Authentication auth) {
        return cartService.getByUserId(getUser(auth).getId());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Update quantity of cart item in the shopping cart by id")
    public void updateQuantity(Authentication auth, @PathVariable Long cartItemId,
                               @RequestBody @Valid UpdateQuantityRequestDto quantityRequestDto) {
        cartService.updateQuantity(getUser(auth), cartItemId, quantityRequestDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete cart item from the shopping cart by id")
    public void deleteCartItem(Authentication auth, @PathVariable Long cartItemId) {
        cartService.delete(getUser(auth), cartItemId);
    }

    private User getUser(Authentication auth) {
        UserDetails details = (UserDetails) auth.getPrincipal();
        String email = details.getUsername();
        return userService.findByEmail(email);
    }
}
