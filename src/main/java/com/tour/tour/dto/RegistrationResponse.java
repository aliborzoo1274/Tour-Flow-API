package com.tour.tour.dto;

public class RegistrationResponse {

    private Long id;
    private Long travelId;
    private String travelName;
    private Long travelerId;
    private String travelerNid;
    private String paymentStatus;

    public RegistrationResponse(Long id, Long travelId, String travelName, Long travelerId, String travelerNid, String paymentStatus) {
        this.id = id;
        this.travelId = travelId;
        this.travelName = travelName;
        this.travelerId = travelerId;
        this.travelerNid = travelerNid;
        this.paymentStatus = paymentStatus;
    }

    public Long getId() {
        return id;
    }

    public Long getTravelId() {
        return travelId;
    }

    public String getTravelName() {
        return travelName;
    }

    public Long getTravelerId() {
        return travelerId;
    }

    public String getTravelerNid() {
        return travelerNid;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }
}
