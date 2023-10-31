package com.example.bookstore.controller;

import com.example.bookstore.dto.CreateOrderRequestDto;
import com.example.bookstore.dto.OrderItemResponseDto;
import com.example.bookstore.dto.OrderResponseDto;
import com.example.bookstore.dto.UpdateOrderRequestDto;
import com.example.bookstore.model.User;
import com.example.bookstore.service.OrderService;
import com.example.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order management", description = "Endpoints for managing orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new order")
    OrderResponseDto createOrder(Authentication auth,
                                 @RequestBody @Valid CreateOrderRequestDto requestDto) {
        return orderService.createOrder(getUser(auth), requestDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    @Operation(summary = "Get all user's orders")
    List<OrderResponseDto> getAllOrders(Authentication auth) {
        return orderService.getAllOrders(getUser(auth));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{orderId}")
    @Operation(summary = "Update order's status by id")
    void updateStatus(@PathVariable Long orderId, @RequestBody UpdateOrderRequestDto requestDto) {
        orderService.updateStatus(orderId, requestDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get all items for a specific order by order's id")
    Set<OrderItemResponseDto> getAllItems(Authentication auth, @PathVariable Long orderId) {
        return orderService.getAllItems(getUser(auth), orderId);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Get specific item by id for a specific order")
    OrderItemResponseDto getItem(Authentication auth, @PathVariable Long orderId,
                                 @PathVariable Long itemId) {
        return orderService.getItem(getUser(auth), orderId, itemId);
    }

    private User getUser(Authentication auth) {
        UserDetails details = (UserDetails) auth.getPrincipal();
        String email = details.getUsername();
        return userService.findByEmail(email);
    }
}
