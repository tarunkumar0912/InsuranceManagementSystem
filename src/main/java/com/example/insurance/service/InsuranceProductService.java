package com.example.insurance.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;

import com.example.insurance.dto.request.InsuranceProductRequest;
import com.example.insurance.dto.response.InsuranceProductResponse;
import com.example.insurance.enums.InsuranceType;

public interface InsuranceProductService {

    InsuranceProductResponse createProduct(
            InsuranceProductRequest request);

    InsuranceProductResponse getProductById(Long id);

    InsuranceProductResponse updateProduct(
            Long id,
            InsuranceProductRequest request);

    InsuranceProductResponse deactivateProduct(Long id);

    Page<InsuranceProductResponse> getAllProducts(
            int page,
            int size,
            String sortBy,
            String direction);

    List<InsuranceProductResponse> getProductsByType(
            InsuranceType insuranceType);

    List<InsuranceProductResponse> searchProductsByCoverageAndPremium(
            BigDecimal minimumCoverage,
            BigDecimal maximumPremium);
}