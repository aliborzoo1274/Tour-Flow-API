package com.tour.tour.dto;

import java.time.LocalDate;
import java.util.List;

public class TravelResponse {
    
    private Long id;
    private String name;
    private Integer capacity;
    private Long cost;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer remainedCapacity;
    private List<String> boardingPlaces;

    public TravelResponse(Long id, String name, Integer capacity, Long cost, LocalDate startDate, LocalDate endDate, Integer remainedCapacity, List<String> boardingPlaces) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.cost = cost;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remainedCapacity = remainedCapacity;
        this.boardingPlaces = boardingPlaces;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public Long getCost() {
        return cost;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Integer getRemainedCapacity() {
        return remainedCapacity;
    }

    public List<String> getBoardingPlaces() {
        return boardingPlaces;
    }
}
