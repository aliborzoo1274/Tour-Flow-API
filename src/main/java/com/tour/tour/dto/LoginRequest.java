package com.tour.tour.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class LoginRequest {

    @NotNull(message = "ID cannot be blank")
    @Positive(message = "ID must be greater than 0")
    private Long id;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
