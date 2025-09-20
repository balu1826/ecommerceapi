package com.example.ecommerce_api.controller;

import com.example.ecommerce_api.model.Product;
import com.example.ecommerce_api.repository.ProductRepository;
import com.example.ecommerce_api.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // -------- Get all products --------
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // -------- Search products --------
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam(required = false) String name,
                                        @RequestParam(required = false) String category) {
        if (name != null) {
            return productRepository.findByNameContainingIgnoreCase(name);
        } else if (category != null) {
            return productRepository.findByCategoryIgnoreCase(category);
        } else {
            return productRepository.findAll();
        }
    }

    // -------- Add product (Admin only) --------
    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody Product product, Authentication auth) {
        if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(403).body("Access denied");
        }
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(saved);
    }

    // -------- Update product (Admin only) --------
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct, Authentication auth) {
        if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(403).body("Access denied");
        }

        return productRepository.findById(id).map(product -> {
            product.setName(updatedProduct.getName());
            product.setCategory(updatedProduct.getCategory());
            product.setPrice(updatedProduct.getPrice());
            product.setQuantity(updatedProduct.getQuantity());
            productRepository.save(product);
            return ResponseEntity.ok(product);
        }).orElse(ResponseEntity.notFound().build());
    }

    // -------- Delete product (Admin only) --------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, Authentication auth) {
        if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ROLE_ADMIN.name()))) {
            return ResponseEntity.status(403).body("Access denied");
        }

        return productRepository.findById(id).map(product -> {
            productRepository.delete(product);
            return ResponseEntity.ok("Deleted successfully");
        }).orElse(ResponseEntity.notFound().build());
    }
}
