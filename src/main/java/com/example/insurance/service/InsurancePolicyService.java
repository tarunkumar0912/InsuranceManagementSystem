package com.example.insurance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.insurance.dto.request.CreatePolicyRequest;
import com.example.insurance.dto.request.UpdatePolicyRequest;
import com.example.insurance.dto.response.InsurancePolicyResponse;
import com.example.insurance.dto.request.RenewPolicyRequest;

public interface InsurancePolicyService {

    InsurancePolicyResponse createPolicy(
            CreatePolicyRequest request);

    InsurancePolicyResponse getPolicyById(Long id);

    InsurancePolicyResponse getPolicyByNumber(
            String policyNumber);

    Page<InsurancePolicyResponse> getAllPolicies(
            Pageable pageable);

    Page<InsurancePolicyResponse> getPoliciesByCustomer(
            Long customerId, Pageable pageable);

    Page<InsurancePolicyResponse> getPoliciesByProduct(
            Long productId, Pageable pageable);

    InsurancePolicyResponse updatePolicy(
            Long id, UpdatePolicyRequest request);

    InsurancePolicyResponse cancelPolicy(Long id);
    
    InsurancePolicyResponse renewPolicy(
            Long id, RenewPolicyRequest request);
}