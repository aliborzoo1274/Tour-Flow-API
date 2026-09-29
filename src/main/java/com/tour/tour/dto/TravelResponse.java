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
    private boolean registrationClosed;
    private String description;
    private String coverImagePath;
    private List<String> imagePaths;

    public TravelResponse(Long id, String name, Integer capacity, Long cost, LocalDate startDate, LocalDate endDate, Integer remainedCapacity, List<String> boardingPlaces, boolean registrationClosed, String description, String coverImagePath, List<String> imagePaths) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.cost = cost;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remainedCapacity = remainedCapacity;
        this.boardingPlaces = boardingPlaces;
        this.registrationClosed = registrationClosed;
        this.description = description;
        this.coverImagePath = coverImagePath;
        this.imagePaths = imagePaths;
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

    public boolean isRegistrationClosed() {
        return registrationClosed;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }
}
