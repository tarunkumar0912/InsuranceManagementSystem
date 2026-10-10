package com.example.insurance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.insurance.entity.InsurancePayment;

public interface InsurancePaymentRepository
        extends JpaRepository<InsurancePayment, Long> {

    List<InsurancePayment> findByPolicy_IdOrderByPaymentDateDesc(
            Long policyId);

    boolean existsByTransactionReference(String transactionReference);
}