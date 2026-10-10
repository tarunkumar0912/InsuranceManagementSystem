package com.example.insurance.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.insurance.entity.InsurancePolicy;
import com.example.insurance.enums.PolicyStatus;
import com.example.insurance.repository.InsurancePolicyRepository;

@Component
public class PolicyExpiryScheduler {

    private static final Logger logger =
            LoggerFactory.getLogger(PolicyExpiryScheduler.class);

    private final InsurancePolicyRepository policyRepository;

    public PolicyExpiryScheduler(
            InsurancePolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Scheduled(cron = "${app.policy.expiry-cron}")
    @Transactional
    public void expirePolicies() {

        LocalDate today = LocalDate.now();

        List<InsurancePolicy> policies =
                policyRepository.findAll();

        int expiredCount = 0;

        for (InsurancePolicy policy : policies) {

            if (policy.getStatus() == PolicyStatus.ACTIVE
                    && policy.getEndDate() != null
                    && policy.getEndDate().isBefore(today)) {

                policy.setStatus(PolicyStatus.EXPIRED);
                expiredCount++;
            }
        }

        policyRepository.saveAll(policies);

        if (expiredCount > 0) {
            logger.info(
                    "Marked {} insurance policies as expired",
                    expiredCount);
        }
    }
}