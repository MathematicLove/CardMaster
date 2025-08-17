package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.AuthRequest;
import com.example.bankcards.dto.auth.AuthResponse;
import com.example.bankcards.dto.user.CreateUserRequest;
import com.example.bankcards.dto.user.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JwtTokenProvider;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

@PostMapping("/login")
public ResponseEntity<?> login(@Valid @RequestBody AuthRequest req) {
    try {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        var principal = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = principal.getUsername();

        String token = jwtTokenProvider.createToken(username, auth.getAuthorities());
        return ResponseEntity.ok(new AuthResponse(token, "Bearer"));
    } catch (org.springframework.security.core.AuthenticationException ex) {
        return ResponseEntity.status(401).body(Map.of(
                "error", "unauthorized",
                "message", "Неверные логин или пароль"
        ));
    }
}

@PostMapping("/register")
public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserRequest req) {
        User u = userService.createUser(req, false);
        return ResponseEntity.ok(UserResponse.from(u));
    }
}