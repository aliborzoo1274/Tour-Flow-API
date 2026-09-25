package com.tour.tour.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "travels")
public class Travel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Long cost;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer remainedCapacity;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "travel_boarding_places", joinColumns = @JoinColumn(name = "travel_id"))
    @Column(name = "boarding_place")
    private List<String> boardingPlaces = new ArrayList<>();

    public Travel() {
    }

    public Travel(String name, Integer capacity, Long cost, LocalDate startDate, LocalDate endDate, Integer remainedCapacity, List<String> boardingPlaces) {
        this.name = name;
        this.capacity = capacity;
        this.cost = cost;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remainedCapacity = remainedCapacity;
        this.boardingPlaces = boardingPlaces != null ? boardingPlaces : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getRemainedCapacity() {
        return remainedCapacity;
    }

    public void setRemainedCapacity(Integer remainedCapacity) {
        this.remainedCapacity = remainedCapacity;
    }

    public List<String> getBoardingPlaces() {
        return boardingPlaces;
    }

    public void setBoardingPlaces(List<String> boardingPlaces) {
        this.boardingPlaces = boardingPlaces != null ? boardingPlaces : new ArrayList<>();
    }
}
