package com.example.bookstore.service;

import com.example.bookstore.dto.CartItemRequestDto;
import com.example.bookstore.dto.ShoppingCartResponseDto;
import com.example.bookstore.dto.UpdateQuantityRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.CartItemMapper;
import com.example.bookstore.mapper.ShoppingCartMapper;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.CartItemRepository;
import com.example.bookstore.repository.ShoppingCartRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public void addToCart(User user, CartItemRequestDto cartItemRequestDto) {
        ShoppingCart cart = shoppingCartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found."));
        Optional<CartItem> cartItem = cartItemRepository
                .findByBookId(cartItemRequestDto.getBookId());
        if (cartItem.isPresent()) {
            cartItem.get().setQuantity(cartItemRequestDto.getQuantity());
        } else {
            CartItem item = cartItemMapper.toModel(cartItemRequestDto);
            item.setShoppingCart(cart);
            cartItemRepository.save(item);
        }
        shoppingCartRepository.save(cart);
    }

    @Override
    public void registerNewShoppingCart(User user) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);
        shoppingCartRepository.save(cart);
    }

    @Override
    public ShoppingCartResponseDto getByUserId(Long id) {
        return shoppingCartMapper.toDto(shoppingCartRepository.findByUser_Id(id)
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found.")));
    }

    @Override
    public void updateQuantity(User user, Long cartItemId, UpdateQuantityRequestDto requestDto) {
        ShoppingCart cart = shoppingCartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found."));
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart item with id " + cartItemId + " not found."));
        cartItem.setQuantity(requestDto.getQuantity());
        cartItemRepository.save(cartItem);
        shoppingCartRepository.save(cart);
    }

    @Override
    public void delete(User user, Long cartItemId) {
        ShoppingCart cart = shoppingCartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found."));
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart item with id " + cartItemId + " not found."));
        cartItemRepository.delete(cartItem);
        shoppingCartRepository.save(cart);
    }
}
