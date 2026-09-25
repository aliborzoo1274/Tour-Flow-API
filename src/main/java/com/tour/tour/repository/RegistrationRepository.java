package com.tour.tour.repository;

import com.tour.tour.model.Registration;
import com.tour.tour.model.Travel;
import com.tour.tour.model.Traveler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    long countByTravel(Travel travel);
    boolean existsByTravelAndTraveler(Travel travel, Traveler traveler);
    List<Registration> findByTravel(Travel travel);
    List<Registration> findByTraveler(Traveler traveler);
}
