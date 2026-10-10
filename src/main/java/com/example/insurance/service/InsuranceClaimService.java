package com.example.insurance.service;

import java.util.List;

import com.example.insurance.dto.request.ReviewClaimRequest;
import com.example.insurance.dto.request.SubmitClaimRequest;
import com.example.insurance.dto.response.ClaimResponse;

public interface InsuranceClaimService {

    ClaimResponse submitClaim(SubmitClaimRequest request);

    ClaimResponse getClaimById(Long id);

    List<ClaimResponse> getClaimsByPolicy(Long policyId);

    List<ClaimResponse> getAllClaims();

    ClaimResponse reviewClaim(Long id, ReviewClaimRequest request);
}