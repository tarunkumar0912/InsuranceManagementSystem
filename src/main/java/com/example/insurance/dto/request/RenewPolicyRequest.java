package com.example.insurance.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class RenewPolicyRequest {

    @NotNull(message = "Renewal premium is required")
    @DecimalMin(value = "0.01",
            message = "Renewal premium must be greater than zero")
    private BigDecimal premiumAmount;

    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount;
    }
}