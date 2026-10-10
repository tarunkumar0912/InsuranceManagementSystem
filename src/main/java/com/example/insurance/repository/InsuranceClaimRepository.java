package com.example.insurance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.insurance.entity.InsuranceClaim;

public interface InsuranceClaimRepository
        extends JpaRepository<InsuranceClaim, Long> {

    List<InsuranceClaim> findByPolicy_IdOrderBySubmittedAtDesc(
            Long policyId);
}