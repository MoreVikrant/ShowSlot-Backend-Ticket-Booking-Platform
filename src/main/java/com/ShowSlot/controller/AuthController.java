package com.ShowSlot.controller;

import com.ShowSlot.dto.AuthResponse;
import com.ShowSlot.dto.LoginRequest;
import com.ShowSlot.dto.ProfileResponse;
import com.ShowSlot.entity.Customer;
import com.ShowSlot.repository.CustomerRepository;
import com.ShowSlot.service.ResourceNotFoundException;
import com.ShowSlot.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// controller/AuthController.java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final CustomerRepository customerRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          TokenService tokenService, CustomerRepository customerRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.customerRepository = customerRepository;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.identifier().trim(), request.password()));

        // authentication.getName() is the email returned by CustomerUserDetailsService
        Customer customer = customerRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return new AuthResponse(tokenService.create(authentication), "Bearer",
                tokenService.expiresInSeconds(), ProfileResponse.from(customer));
    }
}
