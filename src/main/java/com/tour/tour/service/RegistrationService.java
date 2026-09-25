package com.tour.tour.service;

import com.tour.tour.dto.PaymentStatusUpdateRequest;
import com.tour.tour.dto.RegistrationResponse;
import com.tour.tour.exception.DuplicateResourceException;
import com.tour.tour.exception.ResourceNotFoundException;
import com.tour.tour.model.Registration;
import com.tour.tour.model.Travel;
import com.tour.tour.model.Traveler;
import com.tour.tour.repository.RegistrationRepository;
import com.tour.tour.repository.TravelRepository;
import com.tour.tour.repository.TravelerRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final TravelRepository travelRepository;
    private final TravelerRepository travelerRepository;

    public RegistrationService(RegistrationRepository registrationRepository, TravelRepository travelRepository, TravelerRepository travelerRepository) {
        this.registrationRepository = registrationRepository;
        this.travelRepository = travelRepository;
        this.travelerRepository = travelerRepository;
    }

    public RegistrationResponse registerForTravel(Long travelId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !auth.getName().startsWith("USER_")) {
            throw new AccessDeniedException("Only logged in travelers can register for travels.");
        }

        String userNid = auth.getName().substring(5);
        Traveler traveler = travelerRepository.findByNid(userNid)
                .orElseThrow(() -> new ResourceNotFoundException("Traveler not found with NID: " + userNid));

        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException("Travel not found with ID: " + travelId));

        if (registrationRepository.existsByTravelAndTraveler(travel, traveler)) {
            throw new DuplicateResourceException("Traveler is already registered for this travel.");
        }

        if (travel.getRemainedCapacity() <= 0) {
            throw new IllegalStateException("This travel has reached its maximum capacity.");
        }

        travel.setRemainedCapacity(travel.getRemainedCapacity() - 1);
        travelRepository.save(travel);

        Registration registration = new Registration(travel, traveler, "UNPAID");
        registration = registrationRepository.save(registration);

        return toResponse(registration);
    }

    public RegistrationResponse updatePaymentStatus(Long registrationId, PaymentStatusUpdateRequest request) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + registrationId));

        registration.setPaymentStatus(request.getPaymentStatus());
        registration = registrationRepository.save(registration);

        return toResponse(registration);
    }

    public List<RegistrationResponse> getRegistrationsForTravel(Long travelId) {
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException("Travel not found with ID: " + travelId));

        return registrationRepository.findByTravel(travel).stream()
                .map(this::toResponse).toList();
    }
    
    public List<RegistrationResponse> getRegistrationsForTraveler(Long travelerId) {
        Traveler traveler = travelerRepository.findById(travelerId)
                .orElseThrow(() -> new ResourceNotFoundException("Traveler not found with ID: " + travelerId));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !auth.getName().equals("USER_" + traveler.getNid())) {
            throw new AccessDeniedException("You do not have permission to view these registrations.");
        }

        return registrationRepository.findByTraveler(traveler).stream()
                .map(this::toResponse).toList();
    }

    private RegistrationResponse toResponse(Registration registration) {
        return new RegistrationResponse(
                registration.getId(),
                registration.getTravel().getId(),
                registration.getTravel().getName(),
                registration.getTraveler().getId(),
                registration.getTraveler().getNid(),
                registration.getPaymentStatus()
        );
    }
}
