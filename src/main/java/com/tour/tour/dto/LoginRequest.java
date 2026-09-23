package com.tour.tour.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class LoginRequest {

    @NotBlank(message = "NID cannot be blank")
    @Pattern(regexp = "^\\d{10}$", message = "NID must be exactly 10 digits")
    private String nid;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
