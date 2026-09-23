package com.tour.tour.repository;

import com.tour.tour.model.Traveler;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TravelerRepository extends JpaRepository<Traveler, Long> {
    Optional<Traveler> findByNid(String nid);
    boolean existsByNid(String nid);
}
