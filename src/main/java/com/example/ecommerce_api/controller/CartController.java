package com.example.ecommerce_api.controller;

import com.example.ecommerce_api.model.CartItem;
import com.example.ecommerce_api.model.Product;
import com.example.ecommerce_api.model.User;
import com.example.ecommerce_api.repository.CartItemRepository;
import com.example.ecommerce_api.repository.ProductRepository;
import com.example.ecommerce_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // -------- Get current user's cart --------
    @GetMapping
    public ResponseEntity<?> getCart(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        List<CartItem> items = cartItemRepository.findByUser(user);
        return ResponseEntity.ok(items);
    }

    // -------- Add product to cart --------
    @PostMapping("/{productId}")
    public ResponseEntity<?> addToCart(@PathVariable Long productId, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        CartItem item = cartItemRepository.findByUserAndProduct(user, product).orElse(new CartItem(user, product, 0));
        item.setQuantity(item.getQuantity() + 1);
        cartItemRepository.save(item);

        return ResponseEntity.ok(item);
    }

    // -------- Remove product from cart --------
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long productId, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        cartItemRepository.findByUserAndProduct(user, product).ifPresent(cartItemRepository::delete);
        return ResponseEntity.ok("Removed successfully");
    }
}
