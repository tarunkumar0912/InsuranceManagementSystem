package com.example.insurance.service;

import java.util.List;

import com.example.insurance.dto.request.RecordPaymentRequest;
import com.example.insurance.dto.response.PaymentResponse;

public interface InsurancePaymentService {

    PaymentResponse recordPayment(RecordPaymentRequest request);

    PaymentResponse getPaymentById(Long id);

    List<PaymentResponse> getPaymentsByPolicy(Long policyId);
}