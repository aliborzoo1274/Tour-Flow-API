package com.tour.tour.dto;

public class RegistrationResponse {

    private Long id;
    private Long travelId;
    private String travelName;
    private Long travelerId;
    private String travelerNid;
    private Long amountPaid;
    private String paymentStatus;
    private String boardingPlace;
    private String receiptImagePath;

    public RegistrationResponse(Long id, Long travelId, String travelName, Long travelerId, String travelerNid, Long amountPaid, String paymentStatus, String boardingPlace, String receiptImagePath) {
        this.id = id;
        this.travelId = travelId;
        this.travelName = travelName;
        this.travelerId = travelerId;
        this.travelerNid = travelerNid;
        this.amountPaid = amountPaid;
        this.paymentStatus = paymentStatus;
        this.boardingPlace = boardingPlace;
        this.receiptImagePath = receiptImagePath;
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

    public Long getAmountPaid() {
        return amountPaid;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getBoardingPlace() {
        return boardingPlace;
    }

    public String getReceiptImagePath() {
        return receiptImagePath;
    }
}
