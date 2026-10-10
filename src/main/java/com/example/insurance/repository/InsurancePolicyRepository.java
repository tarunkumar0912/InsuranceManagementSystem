package com.example.insurance.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.insurance.entity.InsurancePolicy;

public interface InsurancePolicyRepository
        extends JpaRepository<InsurancePolicy, Long> {

    Optional<InsurancePolicy> findByPolicyNumber(String policyNumber);

    boolean existsByPolicyNumber(String policyNumber);

    Page<InsurancePolicy> findByCustomer_Id(
            Long customerId, Pageable pageable);

    Page<InsurancePolicy> findByInsuranceProduct_Id(
            Long productId, Pageable pageable);
}