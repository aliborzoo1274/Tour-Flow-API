package com.tour.tour.service;

import com.tour.tour.dto.TravelRequest;
import com.tour.tour.dto.TravelResponse;
import com.tour.tour.exception.ResourceNotFoundException;
import com.tour.tour.model.Travel;
import com.tour.tour.repository.TravelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TravelService {

    private final TravelRepository travelRepository;

    public TravelService(TravelRepository travelRepository) {
        this.travelRepository = travelRepository;
    }

    public TravelResponse createTravel(TravelRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        Travel travel = new Travel(
                request.getName(),
                request.getCapacity(),
                request.getCost(),
                request.getStartDate(),
                request.getEndDate(),
                request.getCapacity(),
                request.getBoardingPlaces()
        );
        travel = travelRepository.save(travel);
        return toResponse(travel);
    }

    public TravelResponse getTravelById(Long id) {
        Travel travel = travelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Travel not found with ID: " + id));
        return toResponse(travel);
    }

    public List<TravelResponse> getAllTravels() {
        return travelRepository.findAll().stream().map(this::toResponse).toList();
    }

    public TravelResponse updateTravel(Long id, TravelRequest request) {
        Travel travel = travelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Travel not found with ID: " + id));
        
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        int capacityDifference = request.getCapacity() - travel.getCapacity();
        int newRemainedCapacity = travel.getRemainedCapacity() + capacityDifference;

        if (newRemainedCapacity < 0) {
            throw new IllegalStateException("Cannot reduce capacity below the number of registered travelers.");
        }

        travel.setName(request.getName());
        travel.setCapacity(request.getCapacity());
        travel.setCost(request.getCost());
        travel.setStartDate(request.getStartDate());
        travel.setEndDate(request.getEndDate());
        travel.setRemainedCapacity(newRemainedCapacity);
        travel.setBoardingPlaces(request.getBoardingPlaces());
        
        travel = travelRepository.save(travel);
        return toResponse(travel);
    }

    public TravelResponse updateRegistrationStatus(Long id, boolean closed) {
        Travel travel = travelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Travel not found with ID: " + id));
        travel.setRegistrationClosed(closed);
        travel = travelRepository.save(travel);
        return toResponse(travel);
    }

    public void deleteTravel(Long id) {
        if (!travelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Travel not found with ID: " + id);
        }
        travelRepository.deleteById(id);
    }

    private TravelResponse toResponse(Travel travel) {
        return new TravelResponse(
                travel.getId(),
                travel.getName(),
                travel.getCapacity(),
                travel.getCost(),
                travel.getStartDate(),
                travel.getEndDate(),
                travel.getRemainedCapacity(),
                travel.getBoardingPlaces(),
                travel.isRegistrationClosed()
        );
    }
}
