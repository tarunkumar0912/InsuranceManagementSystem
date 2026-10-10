package com.example.insurance.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.insurance.dto.request.ReviewClaimRequest;
import com.example.insurance.dto.request.SubmitClaimRequest;
import com.example.insurance.dto.response.ClaimResponse;
import com.example.insurance.entity.InsuranceClaim;
import com.example.insurance.entity.InsurancePolicy;
import com.example.insurance.enums.ClaimStatus;
import com.example.insurance.enums.PolicyStatus;
import com.example.insurance.repository.InsuranceClaimRepository;
import com.example.insurance.repository.InsurancePolicyRepository;
import com.example.insurance.service.InsuranceClaimService;

@Service
public class InsuranceClaimServiceImpl
        implements InsuranceClaimService {

    private final InsuranceClaimRepository claimRepository;
    private final InsurancePolicyRepository policyRepository;

    public InsuranceClaimServiceImpl(
            InsuranceClaimRepository claimRepository,
            InsurancePolicyRepository policyRepository) {
        this.claimRepository = claimRepository;
        this.policyRepository = policyRepository;
    }

    @Override
    @Transactional
    public ClaimResponse submitClaim(SubmitClaimRequest request) {

        InsurancePolicy policy = policyRepository
                .findById(request.getPolicyId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Policy not found: " + request.getPolicyId()));

        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Claims can only be submitted for active policies");
        }

        if (request.getClaimAmount()
                .compareTo(policy.getInsuranceProduct()
                        .getCoverageAmount()) > 0) {
            throw new IllegalArgumentException(
                    "Claim amount exceeds the product coverage amount");
        }

        LocalDate today = LocalDate.now();

        if (today.isBefore(policy.getStartDate())
                || today.isAfter(policy.getEndDate())) {
            throw new IllegalArgumentException(
                    "Policy is outside its coverage dates");
        }

        InsuranceClaim claim = new InsuranceClaim();
        claim.setPolicy(policy);
        claim.setClaimAmount(request.getClaimAmount());
        claim.setDescription(request.getDescription());
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setSubmittedAt(LocalDateTime.now());

        return toResponse(claimRepository.save(claim));
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimResponse getClaimById(Long id) {

        InsuranceClaim claim = claimRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Claim not found: " + id));

        return toResponse(claim);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getClaimsByPolicy(Long policyId) {

        if (!policyRepository.existsById(policyId)) {
            throw new IllegalArgumentException(
                    "Policy not found: " + policyId);
        }

        return claimRepository
                .findByPolicy_IdOrderBySubmittedAtDesc(policyId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getAllClaims() {

        return claimRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ClaimResponse reviewClaim(
            Long id, ReviewClaimRequest request) {

        InsuranceClaim claim = claimRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Claim not found: " + id));

        ClaimStatus requestedStatus = request.getStatus();

        if (claim.getStatus() != ClaimStatus.SUBMITTED
                && claim.getStatus() != ClaimStatus.UNDER_REVIEW) {
            throw new IllegalArgumentException(
                    "Only submitted or under-review claims can be reviewed");
        }

        if (requestedStatus != ClaimStatus.UNDER_REVIEW
                && requestedStatus != ClaimStatus.APPROVED
                && requestedStatus != ClaimStatus.REJECTED) {
            throw new IllegalArgumentException(
                    "Allowed review statuses are UNDER_REVIEW, APPROVED, REJECTED");
        }

        claim.setStatus(requestedStatus);
        claim.setReviewNotes(request.getReviewNotes());

        return toResponse(claimRepository.save(claim));
    }

    private ClaimResponse toResponse(InsuranceClaim claim) {

        ClaimResponse response = new ClaimResponse();

        response.setId(claim.getId());
        response.setPolicyId(claim.getPolicy().getId());
        response.setPolicyNumber(
                claim.getPolicy().getPolicyNumber());
        response.setClaimAmount(claim.getClaimAmount());
        response.setDescription(claim.getDescription());
        response.setStatus(claim.getStatus());
        response.setSubmittedAt(claim.getSubmittedAt());
        response.setReviewNotes(claim.getReviewNotes());

        return response;
    }
}