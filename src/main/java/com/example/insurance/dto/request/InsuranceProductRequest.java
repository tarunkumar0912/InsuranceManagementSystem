package com.example.insurance.dto.request;

import java.math.BigDecimal;

import com.example.insurance.enums.InsuranceType;
import com.example.insurance.enums.PaymentFrequency;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class InsuranceProductRequest {

    @NotBlank(message = "Product code is required")
    @Pattern(
        regexp = "^[A-Z0-9-]{3,20}$",
        message = "Product code must contain 3-20 uppercase letters, digits or hyphens"
    )
    private String productCode;

    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 100,
            message = "Product name must be between 3 and 100 characters")
    private String productName;

    @NotNull(message = "Insurance type is required")
    private InsuranceType insuranceType;

    @Size(max = 1000,
            message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Coverage amount is required")
    @DecimalMin(value = "0.01",
            message = "Coverage amount must be greater than zero")
    private BigDecimal coverageAmount;

    @NotNull(message = "Premium amount is required")
    @DecimalMin(value = "0.01",
            message = "Premium amount must be greater than zero")
    private BigDecimal premiumAmount;

    @NotNull(message = "Payment frequency is required")
    private PaymentFrequency paymentFrequency;

    @NotNull(message = "Policy term is required")
    @Min(value = 1, message = "Policy term must be at least one year")
    @Max(value = 100, message = "Policy term cannot exceed 100 years")
    private Integer policyTermInYears;

    public InsuranceProductRequest() {
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
}