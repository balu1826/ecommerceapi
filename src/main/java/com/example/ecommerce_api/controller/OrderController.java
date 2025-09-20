package com.example.ecommerce_api.controller;

import com.example.ecommerce_api.model.CartItem;
import com.example.ecommerce_api.model.Order;
import com.example.ecommerce_api.model.User;
import com.example.ecommerce_api.repository.CartItemRepository;
import com.example.ecommerce_api.repository.OrderRepository;
import com.example.ecommerce_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    // -------- Place order from cart --------
    @PostMapping
    public ResponseEntity<?> placeOrder(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        List<CartItem> items = cartItemRepository.findByUser(user);

        if (items.isEmpty()) {
            return ResponseEntity.badRequest().body("Cart is empty");
        }

        // Calculate total price
        double totalPrice = items.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        // Create order
        Order order = new Order(user, items, totalPrice, LocalDateTime.now());
        orderRepository.save(order);

        // Clear cart after placing order
        cartItemRepository.deleteAll(items);

        return ResponseEntity.ok(order);
    }

    // -------- Get all orders for the current user --------
    @GetMapping
    public ResponseEntity<?> getUserOrders(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        List<Order> orders = orderRepository.findByUser(user);
        return ResponseEntity.ok(orders);
    }
}
