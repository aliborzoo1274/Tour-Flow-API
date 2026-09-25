package com.tour.tour.controller;

import com.tour.tour.dto.TravelerRequest;
import com.tour.tour.dto.TravelerResponse;
import com.tour.tour.dto.TravelerUpdateRequest;
import com.tour.tour.service.TravelerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/travelers")
public class TravelerController {

    private final TravelerService travelerService;

    public TravelerController(TravelerService travelerService) {
        this.travelerService = travelerService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<TravelerResponse>> getTravelers() {
        return ResponseEntity.ok(travelerService.getAllTravelers());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<TravelerResponse> getTraveler(@PathVariable Long id) {
        return ResponseEntity.ok(travelerService.getTravelerById(id));
    }

    @PostMapping
    public ResponseEntity<TravelerResponse> createTraveler(@Valid @RequestBody TravelerRequest request) {
        TravelerResponse response = travelerService.createTraveler(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<TravelerResponse> updateTraveler(@PathVariable Long id,
                                                          @Valid @RequestBody TravelerUpdateRequest request) {
        TravelerResponse response = travelerService.updateTraveler(id, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTraveler(@PathVariable Long id) {
        travelerService.deleteTraveler(id);
        return ResponseEntity.noContent().build();
    }
}
