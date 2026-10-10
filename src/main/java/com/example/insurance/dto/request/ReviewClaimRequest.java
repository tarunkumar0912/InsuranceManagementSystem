package com.example.insurance.dto.request;

import com.example.insurance.enums.ClaimStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewClaimRequest {

    @NotNull(message = "Claim status is required")
    private ClaimStatus status;

    @Size(max = 1000)
    private String reviewNotes;

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public String getReviewNotes() {
        return reviewNotes;
    }

    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }
}