package com.tour.tour.dto;

public class TravelResponse {
    
    private Long id;
    private String name;
    private Integer capacity;
    private Long cost;
    private java.time.LocalDate startDate;
    private java.time.LocalDate endDate;
    private Integer remainedCapacity;

    public TravelResponse(Long id, String name, Integer capacity, Long cost, java.time.LocalDate startDate, java.time.LocalDate endDate, Integer remainedCapacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.cost = cost;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remainedCapacity = remainedCapacity;
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

    public java.time.LocalDate getStartDate() {
        return startDate;
    }

    public java.time.LocalDate getEndDate() {
        return endDate;
    }

    public Integer getRemainedCapacity() {
        return remainedCapacity;
    }
}
