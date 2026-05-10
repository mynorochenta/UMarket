package com.UMarket.backend.controller;

import com.UMarket.backend.dto.LoginRequest;
import com.UMarket.backend.dto.LoginResponse;
import com.UMarket.backend.model.User;
import com.UMarket.backend.repository.UserRepository;
import com.UMarket.backend.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow();

            if (!user.isVerified()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("You must verify your email before logging in");
            }

            String token = jwtUtil.generateToken(request.getEmail());

            return ResponseEntity.ok(new LoginResponse(token, user.getEmail(), user.getId()));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }
}
