package com.example.insurance.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.insurance.dto.request.RenewPolicyRequest;

import com.example.insurance.dto.request.CreatePolicyRequest;
import com.example.insurance.dto.request.UpdatePolicyRequest;
import com.example.insurance.dto.response.InsurancePolicyResponse;
import com.example.insurance.service.InsurancePolicyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/policies")
@Validated
public class InsurancePolicyController {

    private final InsurancePolicyService policyService;

    public InsurancePolicyController(
            InsurancePolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InsurancePolicyResponse> createPolicy(
            @Valid @RequestBody CreatePolicyRequest request) {

        InsurancePolicyResponse response =
                policyService.createPolicy(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsurancePolicyResponse> getPolicyById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                policyService.getPolicyById(id));
    }

    @GetMapping("/number/{policyNumber}")
    public ResponseEntity<InsurancePolicyResponse> getPolicyByNumber(
            @PathVariable String policyNumber) {

        return ResponseEntity.ok(
                policyService.getPolicyByNumber(policyNumber));
    }

    @GetMapping
    public ResponseEntity<Page<InsurancePolicyResponse>> getAllPolicies(
            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(
                policyService.getAllPolicies(pageable));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Page<InsurancePolicyResponse>>
            getPoliciesByCustomer(
                    @PathVariable Long customerId,
                    @PageableDefault(size = 10, sort = "id")
                    Pageable pageable) {

        return ResponseEntity.ok(
                policyService.getPoliciesByCustomer(
                        customerId, pageable));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<InsurancePolicyResponse>>
            getPoliciesByProduct(
                    @PathVariable Long productId,
                    @PageableDefault(size = 10, sort = "id")
                    Pageable pageable) {

        return ResponseEntity.ok(
                policyService.getPoliciesByProduct(
                        productId, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InsurancePolicyResponse> updatePolicy(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePolicyRequest request) {

        return ResponseEntity.ok(
                policyService.updatePolicy(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InsurancePolicyResponse> cancelPolicy(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                policyService.cancelPolicy(id));
    }
    
    @PostMapping("/{id}/renew")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InsurancePolicyResponse> renewPolicy(
            @PathVariable Long id,
            @Valid @RequestBody RenewPolicyRequest request) {

        return ResponseEntity.ok(
                policyService.renewPolicy(id, request));
    }
}