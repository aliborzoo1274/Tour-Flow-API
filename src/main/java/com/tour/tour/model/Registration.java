package com.tour.tour.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "travel_id", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Travel travel;

    @ManyToOne(optional = false)
    @JoinColumn(name = "traveler_id", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Traveler traveler;

    @Column(nullable = false)
    private Long amountPaid;

    public Registration() {
    }

    public Registration(Travel travel, Traveler traveler, Long amountPaid) {
        this.travel = travel;
        this.traveler = traveler;
        this.amountPaid = amountPaid != null ? amountPaid : 0L;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Travel getTravel() {
        return travel;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    public Traveler getTraveler() {
        return traveler;
    }

    public void setTraveler(Traveler traveler) {
        this.traveler = traveler;
    }

    public Long getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Long amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getPaymentStatus() {
        if (this.travel == null || this.travel.getCost() == null || this.amountPaid == null) {
            return "UNPAID";
        }
        return this.amountPaid >= this.travel.getCost() ? "PAID" : "UNPAID";
    }
}
