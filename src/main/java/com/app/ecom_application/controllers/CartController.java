package com.app.ecom_application.controllers;

import com.app.ecom_application.dto.CartItemRequest;
import com.app.ecom_application.models.CartItem;
import com.app.ecom_application.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<String> addToCart(@RequestHeader("X-User-ID") String userId,
                                              @RequestBody CartItemRequest request) {
        if (!cartService.addToCart(userId, request)) {
            return ResponseEntity.badRequest().body("Product out of stock or User not found");
        };
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("items/{productId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long productId,
                                               @RequestHeader("X-User-ID") String userId) {
        boolean deleted = cartService.deleteItemFromCart(productId, userId);
        return deleted ? ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }

    @GetMapping
        public ResponseEntity<List<CartItem>> getCartItems(@RequestHeader("X-User-ID") String userId) {
            return ResponseEntity.ok(cartService.getCart(userId));
    }
}
