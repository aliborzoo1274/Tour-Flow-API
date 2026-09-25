package com.tour.tour.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PaymentStatusUpdateRequest {
    
    @NotBlank(message = "Payment status cannot be blank")
    @Pattern(regexp = "^(PAID|UNPAID)$", message = "Payment status must be PAID or UNPAID")
    private String paymentStatus;

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
