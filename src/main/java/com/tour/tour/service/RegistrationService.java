package com.tour.tour.service;

import com.tour.tour.dto.PaymentUpdateRequest;
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
import com.tour.tour.security.SecurityUtils;

import java.util.List;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import com.tour.tour.dto.RegistrationRequest;

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

    public RegistrationResponse registerForTravel(Long travelId, RegistrationRequest request) {
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

        if (travel.getBoardingPlaces() == null || !travel.getBoardingPlaces().contains(request.getBoardingPlace())) {
            throw new IllegalArgumentException("Invalid boarding place selected. Please select a valid boarding place for this travel.");
        }

        travel.setRemainedCapacity(travel.getRemainedCapacity() - 1);
        travelRepository.save(travel);

        String receiptPath = saveReceiptImage(request.getReceipt());
        Registration registration = new Registration(travel, traveler, 0L, request.getBoardingPlace(), receiptPath);
        registration = registrationRepository.save(registration);

        return toResponse(registration);
    }

    public RegistrationResponse updatePaymentAmount(Long registrationId, PaymentUpdateRequest request) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + registrationId));

        registration.setAmountPaid(request.getAmountPaid());
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

        SecurityUtils.verifyOwnership(traveler, "You do not have permission to view these registrations.");

        return registrationRepository.findByTraveler(traveler).stream()
                .map(this::toResponse).toList();
    }

    public RegistrationResponse updateRegistration(Long registrationId, RegistrationRequest request) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + registrationId));

        SecurityUtils.verifyOwnership(registration.getTraveler(), "You do not have permission to update this registration.");

        Travel travel = registration.getTravel();
        if (travel.getBoardingPlaces() == null || !travel.getBoardingPlaces().contains(request.getBoardingPlace())) {
            throw new IllegalArgumentException("Invalid boarding place selected. Please select a valid boarding place for this travel.");
        }

        registration.setBoardingPlace(request.getBoardingPlace());
        
        if (request.getReceipt() != null && !request.getReceipt().isEmpty()) {
            String receiptPath = saveReceiptImage(request.getReceipt());
            registration.setReceiptImagePath(receiptPath);
        }
        
        registration = registrationRepository.save(registration);

        return toResponse(registration);
    }

    public void deleteRegistration(Long registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + registrationId));

        SecurityUtils.verifyOwnership(registration.getTraveler(), "You do not have permission to delete this registration.");

        Travel travel = registration.getTravel();
        travel.setRemainedCapacity(travel.getRemainedCapacity() + 1);

        registrationRepository.delete(registration);
    }

    private String saveReceiptImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            String uploadDir = "uploads/receipts/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/receipts/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not store the receipt file.", e);
        }
    }

    private RegistrationResponse toResponse(Registration registration) {
        return new RegistrationResponse(
                registration.getId(),
                registration.getTravel().getId(),
                registration.getTravel().getName(),
                registration.getTraveler().getId(),
                registration.getTraveler().getNid(),
                registration.getAmountPaid(),
                registration.getPaymentStatus(),
                registration.getBoardingPlace(),
                registration.getReceiptImagePath()
        );
    }
}
