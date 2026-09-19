package com.tour.tour.controller;

import com.tour.tour.dto.AdminLoginRequest;
import com.tour.tour.dto.AuthResponse;
import com.tour.tour.dto.LoginRequest;
import com.tour.tour.security.JwtService;
import com.tour.tour.security.TourUserDetailsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TourUserDetailsService users;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, TourUserDetailsService users) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.users = users;
    }

    @PostMapping("/login/user")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request) {

        String id = String.valueOf(request.getId());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(id, request.getPassword())
        );

        var user = users.loadUserByUsername(id);
        var token = jwtService.generateToken(user);
        
        return ResponseEntity.ok(new AuthResponse(token, "USER"));
    }

    @PostMapping("/login/admin")
    public ResponseEntity<AuthResponse> loginAdmin(@Valid @RequestBody AdminLoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        var user = users.loadUserByUsername(request.getUsername());
        var token = jwtService.generateToken(user);
        
        return ResponseEntity.ok(new AuthResponse(token, "ADMIN"));
    }
}
