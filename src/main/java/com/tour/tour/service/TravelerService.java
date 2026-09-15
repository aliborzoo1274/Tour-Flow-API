package com.tour.tour.service;

import com.tour.tour.dto.TravelerRequest;
import com.tour.tour.dto.TravelerResponse;
import com.tour.tour.dto.TravelerUpdateRequest;
import com.tour.tour.exception.ResourceNotFoundException;
import com.tour.tour.exception.DuplicateResourceException;
import com.tour.tour.model.Traveler;
import com.tour.tour.model.Role;
import com.tour.tour.repository.TravelerRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

@Service
public class TravelerService {

    private final TravelerRepository travelerRepository;
    private final PasswordEncoder passwordEncoder;

    public TravelerService(TravelerRepository travelerRepository, PasswordEncoder passwordEncoder) {
        this.travelerRepository = travelerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<TravelerResponse> getAllTravelers() {
        return travelerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TravelerResponse getTravelerById(Long id) {
        Traveler traveler = travelerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Traveler not found with id: " + id));

        return toResponse(traveler);
    }

    public TravelerResponse createTraveler(TravelerRequest request) {

        if (travelerRepository.existsById(request.getId())) {
            throw new DuplicateResourceException(
                    "A traveler with this ID already exists"
            );
        }

        Traveler traveler = new Traveler(
                request.getId(),
                request.getName(),
                request.getSurname(),
                request.getAge(),
                passwordEncoder.encode(request.getPassword()),
                "UNPAYED",
                Role.USER
        );

        Traveler savedTraveler = travelerRepository.save(traveler);
        return toResponse(savedTraveler);
    }

    public TravelerResponse updateTraveler(Long id, TravelerUpdateRequest request) {
        Traveler traveler = travelerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Traveler not found with id: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        traveler.setName(request.getName());
        traveler.setSurname(request.getSurname());
        traveler.setAge(request.getAge());
        
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            traveler.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (!request.getPaymentStatus().equals(traveler.getPaymentStatus())) {
            if (!isAdmin) {
                throw new AccessDeniedException("You do not have permission to modify the payment status.");
            }
            traveler.setPaymentStatus(request.getPaymentStatus());
        }

        if (!request.getRole().equalsIgnoreCase(traveler.getRole().name())) {
            if (!isAdmin) {
                throw new AccessDeniedException("You do not have permission to modify the role.");
            }
            try {
                Role role = Role.valueOf(request.getRole().trim().toUpperCase());
                traveler.setRole(role);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid role: " + request.getRole());
            }
        }

        Traveler updatedTraveler = travelerRepository.save(traveler);
        return toResponse(updatedTraveler);
    }

    public void deleteTraveler(Long id) {

        if (!travelerRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Traveler not found with id: " + id);
        }

        travelerRepository.deleteById(id);
    }

    private TravelerResponse toResponse(Traveler traveler) {

        return new TravelerResponse(
                traveler.getId(),
                traveler.getName(),
                traveler.getSurname(),
                traveler.getAge(),
                traveler.getRole().name(),
                traveler.getPaymentStatus()
        );
    }
}
