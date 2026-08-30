package com.tour.tour.service;

import com.tour.tour.dto.TravelerRequest;
import com.tour.tour.dto.TravelerResponse;
import com.tour.tour.exception.TravelerNotFoundException;
import com.tour.tour.model.Traveler;
import com.tour.tour.repository.TravelerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TravelerService {

    private final TravelerRepository travelerRepository;

    public TravelerService(TravelerRepository travelerRepository) {
        this.travelerRepository = travelerRepository;
    }

    public List<TravelerResponse> getAllTravelers() {
        return travelerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TravelerResponse getTravelerById(Long id) {
        Traveler traveler = travelerRepository.findById(id)
                .orElseThrow(() -> new TravelerNotFoundException(id));

        return toResponse(traveler);
    }

    public TravelerResponse createTraveler(TravelerRequest request) {
        Traveler traveler = new Traveler(
                request.getName(),
                request.getSurname(),
                request.getAge(),
                "UNPAYED"
        );

        Traveler savedTraveler = travelerRepository.save(traveler);
        return toResponse(savedTraveler);
    }

    public TravelerResponse updateTraveler(Long id, TravelerRequest request) {
        Traveler traveler = travelerRepository.findById(id)
                .orElseThrow(() -> new TravelerNotFoundException(id));

        traveler.setName(request.getName());
        traveler.setSurname(request.getSurname());
        traveler.setAge(request.getAge());
        traveler.setPaymentStatus("UNPAYED");

        Traveler updatedTraveler = travelerRepository.save(traveler);
        return toResponse(updatedTraveler);
    }

    public void deleteTraveler(Long id) {
        if (!travelerRepository.existsById(id)) {
            throw new TravelerNotFoundException(id);
        }

        travelerRepository.deleteById(id);
    }

    private TravelerResponse toResponse(Traveler traveler) {
        return new TravelerResponse(
                traveler.getName(),
                traveler.getSurname(),
                traveler.getAge(),
                traveler.getPaymentStatus()
        );
    }
}
