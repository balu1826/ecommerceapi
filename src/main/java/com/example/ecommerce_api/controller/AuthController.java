package com.example.ecommerce_api.controller;

import com.example.ecommerce_api.model.User;
import com.example.ecommerce_api.repository.UserRepository;
import com.example.ecommerce_api.security.JwtTokenProvider;
import com.example.ecommerce_api.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // -------- Register --------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        Set<Role> roles = Collections.singleton(Role.ROLE_CUSTOMER);
        User user = new User(username, passwordEncoder.encode(password), roles);
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    // -------- Login --------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        String token = jwtTokenProvider.generateToken(username, user.getRoles());
        return ResponseEntity.ok(Map.of("username", username, "token", token));
    }
}
