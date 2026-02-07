package com.app.ecom_application.services;

import com.app.ecom_application.dto.OrderItemDto;
import com.app.ecom_application.dto.OrderResponse;
import com.app.ecom_application.models.*;
import com.app.ecom_application.repositories.OrderRepository;
import com.app.ecom_application.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartService cartService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public Optional<OrderResponse> createOrder(String userId) {
        // Validate for cart items
        List<CartItem> cartItems = cartService.getCart(userId);
        if(cartItems == null || cartItems.isEmpty()) {
            return Optional.empty();
        }
        // Validate for user
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if(userOpt.isEmpty()) {
            return Optional.empty();
        }
        User user = userOpt.get();
        // Calculate total price
        BigDecimal totalPrice = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);
        List<OrderItem> orderItems = cartItems.stream()
                        .map(item -> new OrderItem(
                                null,
                                item.getProduct(),
                                item.getQuantity(),
                                item.getPrice(),
                                order
                        )).toList();
        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        // Clear the cart
        cartService.clearCart(userId);
        return Optional.of(convertToResponse(savedOrder));
    }

    private OrderResponse convertToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getItems().stream()
                        .map(item -> new OrderItemDto(
                                item.getId(),
                                item.getProduct().getId(),
                                item.getQuantity(),
                                item.getPrice(),
                                item.getPrice().multiply(new BigDecimal(item.getQuantity()))
                        )).toList(),
                order.getCreatedAt()
        );
    }
}
