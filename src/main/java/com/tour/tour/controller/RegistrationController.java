package com.tour.tour.controller;

import com.tour.tour.dto.PaymentStatusUpdateRequest;
import com.tour.tour.dto.RegistrationResponse;
import com.tour.tour.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/travels/{travelId}/register")
    public ResponseEntity<RegistrationResponse> registerForTravel(@PathVariable Long travelId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registrationService.registerForTravel(travelId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/registrations/{registrationId}/payment-status")
    public ResponseEntity<RegistrationResponse> updatePaymentStatus(
            @PathVariable Long registrationId,
            @Valid @RequestBody PaymentStatusUpdateRequest request) {
        return ResponseEntity.ok(registrationService.updatePaymentStatus(registrationId, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/travels/{travelId}/registrations")
    public ResponseEntity<List<RegistrationResponse>> getRegistrationsForTravel(@PathVariable Long travelId) {
        return ResponseEntity.ok(registrationService.getRegistrationsForTravel(travelId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/travelers/{travelerId}/registrations")
    public ResponseEntity<List<RegistrationResponse>> getRegistrationsForTraveler(@PathVariable Long travelerId) {
        return ResponseEntity.ok(registrationService.getRegistrationsForTraveler(travelerId));
    }
}
