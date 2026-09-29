package com.tour.tour.dto;

import java.time.LocalDate;

public class CancellationRuleResponse {
    
    private LocalDate fromDate;
    private LocalDate toDate;
    private int penaltyPercentage;

    public CancellationRuleResponse(LocalDate fromDate, LocalDate toDate, int penaltyPercentage) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.penaltyPercentage = penaltyPercentage;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public int getPenaltyPercentage() {
        return penaltyPercentage;
    }

    public void setPenaltyPercentage(int penaltyPercentage) {
        this.penaltyPercentage = penaltyPercentage;
    }
}
