package com.example.insurance.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.insurance.dto.request.RecordPaymentRequest;
import com.example.insurance.dto.response.PaymentResponse;
import com.example.insurance.entity.InsurancePayment;
import com.example.insurance.entity.InsurancePolicy;
import com.example.insurance.enums.PolicyStatus;
import com.example.insurance.repository.InsurancePaymentRepository;
import com.example.insurance.repository.InsurancePolicyRepository;
import com.example.insurance.service.InsurancePaymentService;

@Service
public class InsurancePaymentServiceImpl
        implements InsurancePaymentService {

    private final InsurancePaymentRepository paymentRepository;
    private final InsurancePolicyRepository policyRepository;

    public InsurancePaymentServiceImpl(
            InsurancePaymentRepository paymentRepository,
            InsurancePolicyRepository policyRepository) {
        this.paymentRepository = paymentRepository;
        this.policyRepository = policyRepository;
    }

    @Override
    @Transactional
    public PaymentResponse recordPayment(RecordPaymentRequest request) {

        if (paymentRepository.existsByTransactionReference(
                request.getTransactionReference())) {
            throw new IllegalArgumentException(
                    "Transaction reference already exists");
        }

        InsurancePolicy policy = policyRepository
                .findById(request.getPolicyId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Policy not found: " + request.getPolicyId()));

        if (policy.getStatus() == PolicyStatus.CANCELLED
                || policy.getStatus() == PolicyStatus.EXPIRED) {
            throw new IllegalArgumentException(
                    "Cannot record a payment for a cancelled or expired policy");
        }

        InsurancePayment payment = new InsurancePayment();
        payment.setPolicy(policy);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(request.getPaymentStatus());
        payment.setTransactionReference(
                request.getTransactionReference());
        payment.setPaymentDate(LocalDateTime.now());

        return toResponse(paymentRepository.save(payment));
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {

        InsurancePayment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment not found: " + id));

        return toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByPolicy(Long policyId) {

        if (!policyRepository.existsById(policyId)) {
            throw new IllegalArgumentException(
                    "Policy not found: " + policyId);
        }

        return paymentRepository
                .findByPolicy_IdOrderByPaymentDateDesc(policyId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PaymentResponse toResponse(InsurancePayment payment) {

        PaymentResponse response = new PaymentResponse();

        response.setId(payment.getId());
        response.setPolicyId(payment.getPolicy().getId());
        response.setPolicyNumber(
                payment.getPolicy().getPolicyNumber());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setPaymentStatus(payment.getPaymentStatus());
        response.setTransactionReference(
                payment.getTransactionReference());
        response.setPaymentDate(payment.getPaymentDate());

        return response;
    }
}