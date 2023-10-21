package com.example.bookstore.service;

import com.example.bookstore.dto.CreateOrderRequestDto;
import com.example.bookstore.dto.OrderItemResponseDto;
import com.example.bookstore.dto.OrderResponseDto;
import com.example.bookstore.dto.UpdateOrderRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.OrderItemMapper;
import com.example.bookstore.mapper.OrderMapper;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.OrderItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.CartItemRepository;
import com.example.bookstore.repository.OrderItemRepository;
import com.example.bookstore.repository.OrderRepository;
import com.example.bookstore.repository.ShoppingCartRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderResponseDto createOrder(User user, CreateOrderRequestDto requestDto) {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);
        order.setUser(user);
        order.setShippingAddress(requestDto.getShippingAddress());
        ShoppingCart shoppingCart = cartRepository.findByUser_Id(user.getId()).orElseThrow();
        Set<OrderItem> orderItems = shoppingCart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setBook(cartItem.getBook());
                    orderItem.setPrice(cartItem.getBook().getPrice());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toSet());
        order.setOrderItems(orderItems);
        order.setTotal(orderItems.stream()
                        .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);
        cartItemRepository.deleteAll(shoppingCart.getCartItems());
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderResponseDto> getAllOrders(User user) {
        return orderRepository.findByUserId(user.getId()).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public void updateStatus(Long orderId, UpdateOrderRequestDto requestDto) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new EntityNotFoundException("Order with id " + orderId + " not found"));
        order.setStatus(requestDto.getStatus());
        orderRepository.save(order);
    }

    @Override
    public Set<OrderItemResponseDto> getAllItems(User user, Long orderId) {
        OrderResponseDto order = getUsersOrderById(user, orderId);
        return order.getOrderItems();
    }

    @Override
    public OrderItemResponseDto getItem(User user, Long orderId, Long itemId) {
        OrderResponseDto order = getUsersOrderById(user, orderId);
        return order.getOrderItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order item with id " + itemId + " not found"));
    }

    private OrderResponseDto getUsersOrderById(User user, Long orderId) {
        return getAllOrders(user).stream()
                .filter(i -> i.getId().equals(orderId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order with id " + orderId + " not found"));
    }
}
