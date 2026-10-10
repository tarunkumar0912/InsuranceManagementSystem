package com.example.insurance.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.insurance.dto.request.ReviewClaimRequest;
import com.example.insurance.dto.request.SubmitClaimRequest;
import com.example.insurance.dto.response.ClaimResponse;
import com.example.insurance.entity.InsuranceClaim;
import com.example.insurance.entity.InsurancePolicy;
import com.example.insurance.entity.InsuranceProduct;
import com.example.insurance.enums.ClaimStatus;
import com.example.insurance.enums.PolicyStatus;
import com.example.insurance.repository.InsuranceClaimRepository;
import com.example.insurance.repository.InsurancePolicyRepository;

@ExtendWith(MockitoExtension.class)
class InsuranceClaimServiceImplTest {

    @Mock
    private InsuranceClaimRepository claimRepository;

    @Mock
    private InsurancePolicyRepository policyRepository;

    @InjectMocks
    private InsuranceClaimServiceImpl claimService;

    private InsurancePolicy policy;
    private SubmitClaimRequest submitRequest;

    @BeforeEach
    void setUp() {
        InsuranceProduct product = new InsuranceProduct();
        product.setId(3L);
        product.setCoverageAmount(new BigDecimal("1000000.00"));

        policy = new InsurancePolicy();
        policy.setId(1L);
        policy.setPolicyNumber("POL-TEST-001");
        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setStartDate(LocalDate.now().minusDays(10));
        policy.setEndDate(LocalDate.now().plusDays(365));
        policy.setInsuranceProduct(product);

        submitRequest = new SubmitClaimRequest();
        submitRequest.setPolicyId(1L);
        submitRequest.setClaimAmount(new BigDecimal("10000.00"));
        submitRequest.setDescription("Hospitalization expenses");
    }

    @Test
    void submitClaimSuccessfully() {
        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(policy));

        when(claimRepository.save(any(InsuranceClaim.class)))
                .thenAnswer(invocation -> {
                    InsuranceClaim claim = invocation.getArgument(0);
                    claim.setId(20L);
                    return claim;
                });

        ClaimResponse response = claimService.submitClaim(submitRequest);

        assertEquals(20L, response.getId());
        assertEquals(1L, response.getPolicyId());
        assertEquals(ClaimStatus.SUBMITTED, response.getStatus());
        assertEquals("POL-TEST-001", response.getPolicyNumber());

        verify(claimRepository).save(any(InsuranceClaim.class));
    }

    @Test
    void submitClaimRejectsMissingPolicy() {
        when(policyRepository.findById(1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> claimService.submitClaim(submitRequest));

        assertEquals("Policy not found: 1", exception.getMessage());

        verify(claimRepository, never()).save(any());
    }

    @Test
    void submitClaimRejectsInactivePolicy() {
        policy.setStatus(PolicyStatus.PENDING);

        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(policy));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> claimService.submitClaim(submitRequest));

        assertEquals(
                "Claims can only be submitted for active policies",
                exception.getMessage());

        verify(claimRepository, never()).save(any());
    }

    @Test
    void submitClaimRejectsAmountAboveCoverage() {
        submitRequest.setClaimAmount(new BigDecimal("1500000.00"));

        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(policy));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> claimService.submitClaim(submitRequest));

        assertEquals(
                "Claim amount exceeds the product coverage amount",
                exception.getMessage());

        verify(claimRepository, never()).save(any());
    }

    @Test
    void reviewClaimRejectsAlreadyApprovedClaim() {
        InsuranceClaim claim = new InsuranceClaim();
        claim.setId(20L);
        claim.setPolicy(policy);
        claim.setStatus(ClaimStatus.APPROVED);

        ReviewClaimRequest request = new ReviewClaimRequest();
        request.setStatus(ClaimStatus.REJECTED);
        request.setReviewNotes("Testing invalid transition");

        when(claimRepository.findById(20L))
                .thenReturn(Optional.of(claim));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> claimService.reviewClaim(20L, request));

        assertEquals(
                "Only submitted or under-review claims can be reviewed",
                exception.getMessage());

        verify(claimRepository, never()).save(any());
    }
}