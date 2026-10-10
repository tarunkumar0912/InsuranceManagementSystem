package com.example.insurance.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class SubmitClaimRequest {

    @NotNull(message = "Policy ID is required")
    @Positive
    private Long policyId;

    @NotNull(message = "Claim amount is required")
    @DecimalMin(value = "0.01",
            message = "Claim amount must be greater than zero")
    private BigDecimal claimAmount;

    @NotBlank(message = "Claim description is required")
    @Size(max = 1000)
    private String description;

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public BigDecimal getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(BigDecimal claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}