package com.example.insurance.mapper;

import org.springframework.stereotype.Component;

import com.example.insurance.dto.request.InsuranceProductRequest;
import com.example.insurance.dto.response.InsuranceProductResponse;
import com.example.insurance.entity.InsuranceProduct;

@Component
public class InsuranceProductMapper {

    public InsuranceProduct toEntity(InsuranceProductRequest request) {

        InsuranceProduct product = new InsuranceProduct();

        product.setProductCode(request.getProductCode());
        product.setProductName(request.getProductName());
        product.setInsuranceType(request.getInsuranceType());
        product.setDescription(request.getDescription());
        product.setCoverageAmount(request.getCoverageAmount());
        product.setPremiumAmount(request.getPremiumAmount());
        product.setPaymentFrequency(request.getPaymentFrequency());
        product.setPolicyTermInYears(request.getPolicyTermInYears());

        return product;
    }

    public InsuranceProductResponse toResponse(
            InsuranceProduct product) {

        InsuranceProductResponse response =
                new InsuranceProductResponse();

        response.setId(product.getId());
        response.setProductCode(product.getProductCode());
        response.setProductName(product.getProductName());
        response.setInsuranceType(product.getInsuranceType());
        response.setDescription(product.getDescription());
        response.setCoverageAmount(product.getCoverageAmount());
        response.setPremiumAmount(product.getPremiumAmount());
        response.setPaymentFrequency(product.getPaymentFrequency());
        response.setPolicyTermInYears(product.getPolicyTermInYears());
        response.setActive(product.isActive());
        response.setCreatedDate(product.getCreatedDate());
        response.setLastModifiedDate(product.getLastModifiedDate());

        return response;
    }
}