package com.example.insurance.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class UpdatePolicyRequest {

    @NotNull(message = "Policy start date is required")
    private LocalDate startDate;

    @NotNull(message = "Policy end date is required")
    private LocalDate endDate;

    @NotNull(message = "Premium amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Premium must be greater than zero")
    private BigDecimal premiumAmount;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount;
    }
}