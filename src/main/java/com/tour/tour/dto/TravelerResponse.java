package com.tour.tour.dto;

public class TravelerResponse {

    private String name;
    private String surname;
    private Integer age;
    private String paymentStatus;

    public TravelerResponse(String name, String surname, Integer age, String paymentStatus) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.paymentStatus = paymentStatus;
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
