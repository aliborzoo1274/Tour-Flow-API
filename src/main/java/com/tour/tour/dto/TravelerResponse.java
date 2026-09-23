package com.tour.tour.dto;

public class TravelerResponse {

    private Long id;
    private String nid;
    private String name;
    private String surname;
    private Integer age;
    private String phoneNumber;
    private String paymentStatus;

    public TravelerResponse(Long id, String nid, String name, String surname, Integer age, String phoneNumber, String paymentStatus) {
        this.id = id;
        this.nid = nid;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.paymentStatus = paymentStatus;
    }

    public Long getId() {
        return id;
    }

    public String getNid() {
        return nid;
    }
    
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Integer getAge() {
        return age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }
}
