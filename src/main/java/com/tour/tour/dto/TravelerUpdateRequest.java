package com.tour.tour.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class TravelerUpdateRequest {

    @NotBlank(message = "NID cannot be blank")
    @Pattern(regexp = "^\\d{10}$", message = "NID must be exactly 10 digits")
    private String nid;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Surname cannot be blank")
    private String surname;

    @NotNull(message = "Age cannot be null")
    private Integer age;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\d{11}$", message = "Phone number must be exactly 11 digits")
    private String phoneNumber;

    private String password;

    @NotBlank(message = "Payment status cannot be blank")
    private String paymentStatus;

    public TravelerUpdateRequest() {
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}