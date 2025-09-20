package com.example.ecommerce_api.repository;

import com.example.ecommerce_api.model.CartItem;
import com.example.ecommerce_api.model.Product;
import com.example.ecommerce_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    void deleteByUserAndProduct(User user, CartItem product);
    Optional<CartItem> findByUserAndProduct(User user, Product product);
}
