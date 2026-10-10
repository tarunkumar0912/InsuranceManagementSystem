package com.example.insurance.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.insurance.dto.request.RecordPaymentRequest;
import com.example.insurance.dto.response.PaymentResponse;
import com.example.insurance.entity.InsurancePayment;
import com.example.insurance.entity.InsurancePolicy;
import com.example.insurance.enums.PaymentMethod;
import com.example.insurance.enums.PaymentStatus;
import com.example.insurance.enums.PolicyStatus;
import com.example.insurance.repository.InsurancePaymentRepository;
import com.example.insurance.repository.InsurancePolicyRepository;

@ExtendWith(MockitoExtension.class)
class InsurancePaymentServiceImplTest {

    @Mock
    private InsurancePaymentRepository paymentRepository;

    @Mock
    private InsurancePolicyRepository policyRepository;

    @InjectMocks
    private InsurancePaymentServiceImpl paymentService;

    private InsurancePolicy policy;
    private RecordPaymentRequest request;

    @BeforeEach
    void setUp() {
        policy = new InsurancePolicy();
        policy.setId(1L);
        policy.setPolicyNumber("POL-TEST-001");
        policy.setStatus(PolicyStatus.ACTIVE);

        request = new RecordPaymentRequest();
        request.setPolicyId(1L);
        request.setAmount(new BigDecimal("2500.00"));
        request.setPaymentMethod(PaymentMethod.UPI);
        request.setPaymentStatus(PaymentStatus.SUCCESS);
        request.setTransactionReference("TXN-TEST-001");
    }

    @Test
    void recordPaymentSuccessfully() {
        when(paymentRepository.existsByTransactionReference(
                "TXN-TEST-001")).thenReturn(false);

        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(policy));

        when(paymentRepository.save(any(InsurancePayment.class)))
                .thenAnswer(invocation -> {
                    InsurancePayment payment = invocation.getArgument(0);
                    payment.setId(10L);
                    return payment;
                });

        PaymentResponse response = paymentService.recordPayment(request);

        assertEquals(10L, response.getId());
        assertEquals(1L, response.getPolicyId());
        assertEquals("POL-TEST-001", response.getPolicyNumber());
        assertEquals(0,
                new BigDecimal("2500.00").compareTo(response.getAmount()));
        assertEquals(PaymentStatus.SUCCESS, response.getPaymentStatus());

        verify(paymentRepository).save(any(InsurancePayment.class));
    }

    @Test
    void recordPaymentRejectsDuplicateTransactionReference() {
        when(paymentRepository.existsByTransactionReference(
                "TXN-TEST-001")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.recordPayment(request));

        assertEquals("Transaction reference already exists",
                exception.getMessage());

        verify(policyRepository, never()).findById(any());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void recordPaymentRejectsMissingPolicy() {
        when(paymentRepository.existsByTransactionReference(
                "TXN-TEST-001")).thenReturn(false);

        when(policyRepository.findById(1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.recordPayment(request));

        assertEquals("Policy not found: 1", exception.getMessage());

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void recordPaymentRejectsExpiredPolicy() {
        policy.setStatus(PolicyStatus.EXPIRED);

        when(paymentRepository.existsByTransactionReference(
                "TXN-TEST-001")).thenReturn(false);

        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(policy));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.recordPayment(request));

        assertEquals(
                "Cannot record a payment for a cancelled or expired policy",
                exception.getMessage());

        verify(paymentRepository, never()).save(any());
    }
}