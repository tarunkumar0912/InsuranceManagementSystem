package com.example.insurance.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.insurance.entity.InsuranceProduct;
import com.example.insurance.enums.InsuranceType;

public interface InsuranceProductRepository
        extends JpaRepository<InsuranceProduct, Long> {

    boolean existsByProductCode(String productCode);

    boolean existsByProductCodeAndIdNot(String productCode, Long id);

    Page<InsuranceProduct> findByActiveTrue(Pageable pageable);

    List<InsuranceProduct> findByInsuranceTypeAndActiveTrue(
            InsuranceType insuranceType);

    @Query("""
            SELECT p
            FROM InsuranceProduct p
            WHERE p.active = true
              AND p.insuranceType = :insuranceType
            ORDER BY p.premiumAmount ASC
            """)
    List<InsuranceProduct> findActiveProductsByType(
            @Param("insuranceType") InsuranceType insuranceType);

    @Query(value = """
            SELECT *
            FROM insurance_products
            WHERE active = true
              AND coverage_amount >= :minimumCoverage
              AND premium_amount <= :maximumPremium
            ORDER BY premium_amount ASC
            """, nativeQuery = true)
    List<InsuranceProduct> findProductsByCoverageAndPremium(
            @Param("minimumCoverage") BigDecimal minimumCoverage,
            @Param("maximumPremium") BigDecimal maximumPremium);
}