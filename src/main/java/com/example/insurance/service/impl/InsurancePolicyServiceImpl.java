package com.example.insurance.service.impl;

import java.time.LocalDate;
import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.insurance.dto.request.CreatePolicyRequest;
import com.example.insurance.dto.request.UpdatePolicyRequest;
import com.example.insurance.dto.response.InsurancePolicyResponse;
import com.example.insurance.entity.Customer;
import com.example.insurance.entity.InsurancePolicy;
import com.example.insurance.entity.InsuranceProduct;
import com.example.insurance.enums.PolicyStatus;
import com.example.insurance.mapper.InsurancePolicyMapper;
import com.example.insurance.repository.CustomerRepository;
import com.example.insurance.repository.InsurancePolicyRepository;
import com.example.insurance.repository.InsuranceProductRepository;
import com.example.insurance.service.InsurancePolicyService;
import com.example.insurance.dto.request.RenewPolicyRequest;

@Service
public class InsurancePolicyServiceImpl
        implements InsurancePolicyService {

    private final InsurancePolicyRepository policyRepository;
    private final CustomerRepository customerRepository;
    private final InsuranceProductRepository productRepository;
    private final InsurancePolicyMapper policyMapper;

    public InsurancePolicyServiceImpl(
            InsurancePolicyRepository policyRepository,
            CustomerRepository customerRepository,
            InsuranceProductRepository productRepository,
            InsurancePolicyMapper policyMapper) {

        this.policyRepository = policyRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.policyMapper = policyMapper;
    }

    @Override
    @Transactional
    public InsurancePolicyResponse createPolicy(
            CreatePolicyRequest request) {

        validateDates(
                request.getStartDate(),
                request.getEndDate());

        Customer customer = customerRepository
                .findById(request.getCustomerId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer not found: "
                                        + request.getCustomerId()));

        InsuranceProduct product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Insurance product not found: "
                                        + request.getProductId()));

        if (!product.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot create a policy for an inactive product");
        }

        InsurancePolicy policy = new InsurancePolicy();

        policy.setPolicyNumber(generatePolicyNumber());
        policy.setCustomer(customer);
        policy.setInsuranceProduct(product);
        policy.setStartDate(request.getStartDate());
        policy.setEndDate(request.getEndDate());
        policy.setPremiumAmount(request.getPremiumAmount());
        policy.setStatus(PolicyStatus.PENDING);

        InsurancePolicy savedPolicy =
                policyRepository.save(policy);

        return policyMapper.toResponse(savedPolicy);
    }

    @Override
    @Transactional(readOnly = true)
    public InsurancePolicyResponse getPolicyById(Long id) {

        InsurancePolicy policy = findPolicy(id);

        return policyMapper.toResponse(policy);
    }

    @Override
    @Transactional(readOnly = true)
    public InsurancePolicyResponse getPolicyByNumber(
            String policyNumber) {

        InsurancePolicy policy = policyRepository
                .findByPolicyNumber(policyNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Policy not found: " + policyNumber));

        return policyMapper.toResponse(policy);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InsurancePolicyResponse> getAllPolicies(
            Pageable pageable) {

        return policyRepository.findAll(pageable)
                .map(policyMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InsurancePolicyResponse> getPoliciesByCustomer(
            Long customerId, Pageable pageable) {

        if (!customerRepository.existsById(customerId)) {
            throw new IllegalArgumentException(
                    "Customer not found: " + customerId);
        }

        return policyRepository
                .findByCustomer_Id(customerId, pageable)
                .map(policyMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InsurancePolicyResponse> getPoliciesByProduct(
            Long productId, Pageable pageable) {

        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException(
                    "Insurance product not found: " + productId);
        }

        return policyRepository
                .findByInsuranceProduct_Id(productId, pageable)
                .map(policyMapper::toResponse);
    }

    @Override
    @Transactional
    public InsurancePolicyResponse updatePolicy(
            Long id, UpdatePolicyRequest request) {

        InsurancePolicy policy = findPolicy(id);

        if (policy.getStatus() == PolicyStatus.CANCELLED
                || policy.getStatus() == PolicyStatus.EXPIRED) {
            throw new IllegalArgumentException(
                    "Cancelled or expired policies cannot be updated");
        }

        validateDates(
                request.getStartDate(),
                request.getEndDate());

        policy.setStartDate(request.getStartDate());
        policy.setEndDate(request.getEndDate());
        policy.setPremiumAmount(request.getPremiumAmount());

        return policyMapper.toResponse(
                policyRepository.save(policy));
    }

    @Override
    @Transactional
    public InsurancePolicyResponse cancelPolicy(Long id) {

        InsurancePolicy policy = findPolicy(id);

        if (policy.getStatus() == PolicyStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Policy is already cancelled");
        }

        if (policy.getStatus() == PolicyStatus.EXPIRED) {
            throw new IllegalArgumentException(
                    "An expired policy cannot be cancelled");
        }

        policy.setStatus(PolicyStatus.CANCELLED);

        return policyMapper.toResponse(
                policyRepository.save(policy));
    }

    private InsurancePolicy findPolicy(Long id) {

        return policyRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Policy not found: " + id));
    }

    private void validateDates(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Start date and end date are required");
        }

        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException(
                    "End date must be after start date");
        }
    }

    private String generatePolicyNumber() {

        String policyNumber;

        do {
            int randomNumber =
                    ThreadLocalRandom.current()
                            .nextInt(100000, 1000000);

            policyNumber = "POL-"
                    + Year.now().getValue()
                    + "-"
                    + randomNumber;

        } while (policyRepository
                .existsByPolicyNumber(policyNumber));

        return policyNumber;
    }
    @Override
    @Transactional
    public InsurancePolicyResponse renewPolicy(
            Long id, RenewPolicyRequest request) {

        InsurancePolicy policy = findPolicy(id);

        if (policy.getStatus() == PolicyStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "A cancelled policy cannot be renewed");
        }

        if (policy.getStatus() != PolicyStatus.ACTIVE
                && policy.getStatus() != PolicyStatus.EXPIRED) {
            throw new IllegalArgumentException(
                    "Only active or expired policies can be renewed");
        }

        LocalDate oldStartDate = policy.getStartDate();
        LocalDate oldEndDate = policy.getEndDate();

        if (oldEndDate == null || oldStartDate == null
                || !oldEndDate.isAfter(oldStartDate)) {
            throw new IllegalArgumentException(
                    "Existing policy dates are invalid");
        }

        long policyDurationDays =
                java.time.temporal.ChronoUnit.DAYS.between(
                        oldStartDate, oldEndDate);

        LocalDate renewalStartDate = LocalDate.now();

        // An active policy must not be renewed before its current
        // coverage ends in this simplified implementation.
        if (policy.getStatus() == PolicyStatus.ACTIVE
                && !renewalStartDate.isAfter(oldEndDate)) {
            throw new IllegalArgumentException(
                    "An active policy cannot be renewed before its end date");
        }

        LocalDate renewalEndDate =
                renewalStartDate.plusDays(policyDurationDays);

        policy.setStartDate(renewalStartDate);
        policy.setEndDate(renewalEndDate);
        policy.setPremiumAmount(request.getPremiumAmount());
        policy.setStatus(PolicyStatus.ACTIVE);

        return policyMapper.toResponse(
                policyRepository.save(policy));
    }
    
    
}