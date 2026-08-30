package com.tour.tour.dto;

public class TravelerResponse {

    private Long id;
    private String name;
    private String surname;
    private Integer age;
    private String paymentStatus;

    public TravelerResponse(Long id, String name, String surname, Integer age, String paymentStatus) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.paymentStatus = paymentStatus;
    }

    public Long getId() {
        return id;
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

    public String getPaymentStatus() {
        return paymentStatus;
    }
}
