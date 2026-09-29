package com.tour.tour.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class TravelRequest {
    
    @NotBlank(message = "Travel name cannot be blank")
    private String name;

    @NotNull(message = "Capacity cannot be null")
    @Positive(message = "Capacity must be greater than 0")
    private Integer capacity;

    @NotNull(message = "Cost cannot be null")
    @Min(value = 0, message = "Cost cannot be negative")
    private Long cost;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    private LocalDate endDate;

    private List<String> boardingPlaces;

    private String description;

    private MultipartFile coverImage;

    private List<MultipartFile> images;

    private List<String> existingImages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<String> getBoardingPlaces() {
        return boardingPlaces;
    }

    public void setBoardingPlaces(List<String> boardingPlaces) {
        this.boardingPlaces = boardingPlaces;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(MultipartFile coverImage) {
        this.coverImage = coverImage;
    }

    public List<MultipartFile> getImages() {
        return images;
    }

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }

    public List<String> getExistingImages() {
        return existingImages;
    }

    public void setExistingImages(List<String> existingImages) {
        this.existingImages = existingImages;
    }
}
