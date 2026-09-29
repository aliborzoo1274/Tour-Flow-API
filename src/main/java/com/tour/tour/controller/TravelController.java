package com.tour.tour.controller;

import com.tour.tour.dto.TravelRequest;
import com.tour.tour.dto.TravelResponse;
import com.tour.tour.service.TravelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travels")
public class TravelController {

    private final TravelService travelService;

    public TravelController(TravelService travelService) {
        this.travelService = travelService;
    }

    @GetMapping
    public ResponseEntity<List<TravelResponse>> getAllTravels() {
        return ResponseEntity.ok(travelService.getAllTravels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelResponse> getTravelById(@PathVariable Long id) {
        return ResponseEntity.ok(travelService.getTravelById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TravelResponse> createTravel(@Valid @RequestBody TravelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(travelService.createTravel(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TravelResponse> updateTravel(@PathVariable Long id, @Valid @RequestBody TravelRequest request) {
        return ResponseEntity.ok(travelService.updateTravel(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/registration-status")
    public ResponseEntity<TravelResponse> updateRegistrationStatus(@PathVariable Long id, @RequestParam boolean closed) {
        return ResponseEntity.ok(travelService.updateRegistrationStatus(id, closed));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTravel(@PathVariable Long id) {
        travelService.deleteTravel(id);
        return ResponseEntity.noContent().build();
    }

}
