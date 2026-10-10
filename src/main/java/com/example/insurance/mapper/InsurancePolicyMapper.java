package com.example.insurance.mapper;

import org.springframework.stereotype.Component;

import com.example.insurance.dto.response.InsurancePolicyResponse;
import com.example.insurance.entity.InsurancePolicy;

@Component
public class InsurancePolicyMapper {

    public InsurancePolicyResponse toResponse(
            InsurancePolicy policy) {

        InsurancePolicyResponse response =
                new InsurancePolicyResponse();

        response.setId(policy.getId());
        response.setPolicyNumber(policy.getPolicyNumber());

        response.setCustomerId(policy.getCustomer().getId());
        response.setCustomerName(
                policy.getCustomer().getFullName());

        response.setProductId(
                policy.getInsuranceProduct().getId());

        // Assumes InsuranceProduct has a getProductName() method.
        response.setProductName(
                policy.getInsuranceProduct().getProductName());

        response.setStartDate(policy.getStartDate());
        response.setEndDate(policy.getEndDate());
        response.setPremiumAmount(policy.getPremiumAmount());
        response.setStatus(policy.getStatus());

        response.setCreatedDate(policy.getCreatedDate());
        response.setLastModifiedDate(
                policy.getLastModifiedDate());

        return response;
    }
}