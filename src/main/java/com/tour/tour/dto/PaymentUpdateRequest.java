package com.tour.tour.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PaymentUpdateRequest {
    
    @NotNull(message = "Amount paid cannot be null")
    @Min(value = 0, message = "Amount paid cannot be negative")
    private Long amountPaid;

    public Long getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Long amountPaid) {
        this.amountPaid = amountPaid;
    }
}
