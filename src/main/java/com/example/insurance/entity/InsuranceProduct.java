package com.example.insurance.entity;

import java.math.BigDecimal;

import com.example.insurance.enums.InsuranceType;
import com.example.insurance.enums.PaymentFrequency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

@Entity
@Table(name = "insurance_products")
public class InsuranceProduct extends BaseAuditableEntity {
	
	@OneToMany(mappedBy = "insuranceProduct", fetch = FetchType.LAZY)
	private List<InsurancePolicy> policies = new ArrayList<>();

	public List<InsurancePolicy> getPolicies() {
	    return policies;
	}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String productCode;

    @Column(nullable = false, length = 100)
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InsuranceType insuranceType;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal coverageAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal premiumAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentFrequency paymentFrequency;

    @Column(nullable = false)
    private Integer policyTermInYears;

    @Column(nullable = false)
    private boolean active = true;

    public InsuranceProduct() {
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
}