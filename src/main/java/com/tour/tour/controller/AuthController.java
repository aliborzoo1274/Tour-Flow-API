package com.tour.tour.controller;

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

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

        String id = String.valueOf(request.getId());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(id, request.getPassword())
        );

        var user = users.loadUserByUsername(id);
        
        String role = user.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))
                ? "ADMIN" : "USER";

        var token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token, role));
    }
}
