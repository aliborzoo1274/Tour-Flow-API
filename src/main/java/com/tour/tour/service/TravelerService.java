package com.tour.tour.service;

import com.tour.tour.dto.TravelerRequest;
import com.tour.tour.dto.TravelerResponse;
import com.tour.tour.dto.TravelerUpdateRequest;
import com.tour.tour.exception.ResourceNotFoundException;
import com.tour.tour.exception.DuplicateResourceException;
import com.tour.tour.model.Traveler;
import com.tour.tour.repository.TravelerRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
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
                "Traveler not found with ID: " + id));

        verifyOwnership(traveler);

        return toResponse(traveler);
    }

    public TravelerResponse createTraveler(TravelerRequest request) {

        if (travelerRepository.existsByNid(request.getNid())) {
            throw new DuplicateResourceException(
                    "A traveler with this NID already exists"
            );
        }

        Traveler traveler = new Traveler(
                request.getNid(),
                request.getName(),
                request.getSurname(),
                request.getAge(),
                request.getPhoneNumber(),
                passwordEncoder.encode(request.getPassword()),
                "UNPAID"
        );

        Traveler savedTraveler = travelerRepository.save(traveler);
        return toResponse(savedTraveler);
    }

    public TravelerResponse updateTraveler(Long id, TravelerUpdateRequest request) {
        Traveler traveler = travelerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Traveler not found with ID: " + id));

        verifyOwnership(traveler);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!request.getNid().equals(traveler.getNid())) {
            if (travelerRepository.existsByNid(request.getNid())) {
                throw new DuplicateResourceException("A traveler with this NID already exists");
            }
            traveler.setNid(request.getNid());
        }

        traveler.setName(request.getName());
        traveler.setSurname(request.getSurname());
        traveler.setAge(request.getAge());
        traveler.setPhoneNumber(request.getPhoneNumber());
        
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            traveler.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (!request.getPaymentStatus().equals(traveler.getPaymentStatus())) {
            if (!isAdmin) {
                throw new AccessDeniedException("You do not have permission to modify the payment status.");
            }
            traveler.setPaymentStatus(request.getPaymentStatus());
        }

        Traveler updatedTraveler = travelerRepository.save(traveler);
        return toResponse(updatedTraveler);
    }

    public void deleteTraveler(Long id) {
        Traveler traveler = travelerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Traveler not found with ID: " + id));

        travelerRepository.delete(traveler);
    }

    private void verifyOwnership(Traveler traveler) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication is required.");
        }

        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !("USER_" + traveler.getNid()).equals(authentication.getName())) {
            throw new AccessDeniedException("You do not have permission to access this traveler's data.");
        }
    }

    private TravelerResponse toResponse(Traveler traveler) {

        return new TravelerResponse(
                traveler.getId(),
                traveler.getNid(),
                traveler.getName(),
                traveler.getSurname(),
                traveler.getAge(),
                traveler.getPhoneNumber(),
                traveler.getPaymentStatus()
        );
    }
}
