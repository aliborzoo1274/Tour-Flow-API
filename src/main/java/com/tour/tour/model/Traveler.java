package com.tour.tour.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "travelers")
public class Traveler {

    @Id
    private Long id;
    private String name;
    private String surname;
    private Integer age;
    private String password;
    private String paymentStatus;

    public Traveler() {
    }

    public Traveler(Long id, String name, String surname, Integer age, String password, String paymentStatus) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.password = password;
        this.paymentStatus = paymentStatus;
    }

    public Long getId() {
        return id;
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
