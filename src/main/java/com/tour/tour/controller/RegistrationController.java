package com.tour.tour.controller;

import com.tour.tour.dto.PaymentUpdateRequest;
import com.tour.tour.dto.RegistrationResponse;
import com.tour.tour.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.tour.tour.dto.RegistrationRequest;

@RestController
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/travels/{travelId}/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RegistrationResponse> registerForTravel(
            @PathVariable Long travelId,
            @Valid @ModelAttribute RegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registrationService.registerForTravel(travelId, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/registrations/{registrationId}/payment")
    public ResponseEntity<RegistrationResponse> updatePaymentAmount(
            @PathVariable Long registrationId,
            @Valid @RequestBody PaymentUpdateRequest request) {
        return ResponseEntity.ok(registrationService.updatePaymentAmount(registrationId, request));
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

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PutMapping(value = "/registrations/{registrationId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RegistrationResponse> updateRegistration(
            @PathVariable Long registrationId,
            @Valid @ModelAttribute RegistrationRequest request) {
        return ResponseEntity.ok(registrationService.updateRegistration(registrationId, request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @DeleteMapping("/registrations/{registrationId}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable Long registrationId) {
        registrationService.deleteRegistration(registrationId);
        return ResponseEntity.noContent().build();
    }
}
