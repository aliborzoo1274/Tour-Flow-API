package com.tour.tour.service;

import com.tour.tour.dto.TravelerRequest;
import com.tour.tour.dto.TravelerResponse;
import com.tour.tour.dto.TravelerUpdateRequest;
import com.tour.tour.exception.ResourceNotFoundException;
import com.tour.tour.exception.DuplicateResourceException;
import com.tour.tour.model.Traveler;
import com.tour.tour.repository.TravelerRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tour.tour.model.Registration;
import com.tour.tour.model.Travel;
import com.tour.tour.repository.RegistrationRepository;
import com.tour.tour.security.SecurityUtils;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TravelerService {

    private final TravelerRepository travelerRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegistrationRepository registrationRepository;

    public TravelerService(TravelerRepository travelerRepository, PasswordEncoder passwordEncoder, RegistrationRepository registrationRepository) {
        this.travelerRepository = travelerRepository;
        this.passwordEncoder = passwordEncoder;
        this.registrationRepository = registrationRepository;
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

        SecurityUtils.verifyOwnership(traveler, "You do not have permission to access this traveler's data.");

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
                passwordEncoder.encode(request.getPassword())
        );

        Traveler savedTraveler = travelerRepository.save(traveler);
        return toResponse(savedTraveler);
    }

    public TravelerResponse updateTraveler(Long id, TravelerUpdateRequest request) {
        Traveler traveler = travelerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Traveler not found with ID: " + id));

        SecurityUtils.verifyOwnership(traveler, "You do not have permission to access this traveler's data.");

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

        Traveler updatedTraveler = travelerRepository.save(traveler);
        return toResponse(updatedTraveler);
    }

    public void deleteTraveler(Long id) {
        Traveler traveler = travelerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Traveler not found with ID: " + id));

        SecurityUtils.verifyOwnership(traveler, "You do not have permission to delete this traveler's account.");

        List<Registration> registrations = registrationRepository.findByTraveler(traveler);
        for (Registration reg : registrations) {
            Travel travel = reg.getTravel();
            travel.setRemainedCapacity(travel.getRemainedCapacity() + 1);
        }

        travelerRepository.delete(traveler);
    }

    private TravelerResponse toResponse(Traveler traveler) {

        return new TravelerResponse(
                traveler.getId(),
                traveler.getNid(),
                traveler.getName(),
                traveler.getSurname(),
                traveler.getAge(),
                traveler.getPhoneNumber()
        );
    }
}
