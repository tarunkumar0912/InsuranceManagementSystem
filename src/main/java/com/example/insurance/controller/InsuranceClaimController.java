package com.example.insurance.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.insurance.dto.request.ReviewClaimRequest;
import com.example.insurance.dto.request.SubmitClaimRequest;
import com.example.insurance.dto.response.ClaimResponse;
import com.example.insurance.service.InsuranceClaimService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/claims")
@Validated
public class InsuranceClaimController {

    private final InsuranceClaimService claimService;

    public InsuranceClaimController(
            InsuranceClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    public ResponseEntity<ClaimResponse> submitClaim(
            @Valid @RequestBody SubmitClaimRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(claimService.submitClaim(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimResponse> getClaim(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                claimService.getClaimById(id));
    }

    @GetMapping("/policy/{policyId}")
    public ResponseEntity<List<ClaimResponse>> getClaimsByPolicy(
            @PathVariable Long policyId) {

        return ResponseEntity.ok(
                claimService.getClaimsByPolicy(policyId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClaimResponse>> getAllClaims() {

        return ResponseEntity.ok(
                claimService.getAllClaims());
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClaimResponse> reviewClaim(
            @PathVariable Long id,
            @Valid @RequestBody ReviewClaimRequest request) {

        return ResponseEntity.ok(
                claimService.reviewClaim(id, request));
    }
}