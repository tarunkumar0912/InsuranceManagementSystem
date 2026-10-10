package com.example.insurance.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.insurance.enums.InsuranceType;
import com.example.insurance.enums.PaymentFrequency;

public class InsuranceProductResponse {

    private Long id;
    private String productCode;
    private String productName;
    private InsuranceType insuranceType;
    private String description;
    private BigDecimal coverageAmount;
    private BigDecimal premiumAmount;
    private PaymentFrequency paymentFrequency;
    private Integer policyTermInYears;
    private boolean active;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public InsuranceProductResponse() {
    }

    public Long getId() {
        return id;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public InsuranceType getInsuranceType() {
        return insuranceType;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getCoverageAmount() {
        return coverageAmount;
    }

    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }

    public PaymentFrequency getPaymentFrequency() {
        return paymentFrequency;
    }

    public Integer getPolicyTermInYears() {
        return policyTermInYears;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setInsuranceType(InsuranceType insuranceType) {
        this.insuranceType = insuranceType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCoverageAmount(BigDecimal coverageAmount) {
        this.coverageAmount = coverageAmount;
    }

    public void setPremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public void setPaymentFrequency(PaymentFrequency paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    public void setPolicyTermInYears(Integer policyTermInYears) {
        this.policyTermInYears = policyTermInYears;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}