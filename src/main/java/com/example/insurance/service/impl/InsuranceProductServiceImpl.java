package com.example.insurance.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.insurance.dto.request.InsuranceProductRequest;
import com.example.insurance.dto.response.InsuranceProductResponse;
import com.example.insurance.entity.InsuranceProduct;
import com.example.insurance.enums.InsuranceType;
import com.example.insurance.exception.InsuranceProductAlreadyExistsException;
import com.example.insurance.exception.InsuranceProductNotFoundException;
import com.example.insurance.mapper.InsuranceProductMapper;
import com.example.insurance.repository.InsuranceProductRepository;
import com.example.insurance.service.InsuranceProductService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

@Service
@Transactional
public class InsuranceProductServiceImpl
        implements InsuranceProductService {

    private final InsuranceProductRepository productRepository;
    private final InsuranceProductMapper productMapper;

    public InsuranceProductServiceImpl(
            InsuranceProductRepository productRepository,
            InsuranceProductMapper productMapper) {

        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    @CacheEvict(cacheNames = "insuranceProducts", allEntries = true)
    public InsuranceProductResponse createProduct(
            InsuranceProductRequest request) {

        if (productRepository.existsByProductCode(
                request.getProductCode())) {

            throw new InsuranceProductAlreadyExistsException(
                    "Product code already exists: "
                            + request.getProductCode());
        }

        InsuranceProduct product = productMapper.toEntity(request);

        InsuranceProduct savedProduct =
                productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "insuranceProducts", key = "'product:' + #id")
    public InsuranceProductResponse getProductById(Long id) {

        InsuranceProduct product = findProductOrThrow(id);

        return productMapper.toResponse(product);
    }

    @Override
    @CacheEvict(cacheNames = "insuranceProducts", allEntries = true)
    public InsuranceProductResponse updateProduct(
            Long id,
            InsuranceProductRequest request) {

        InsuranceProduct product = findProductOrThrow(id);

        if (productRepository.existsByProductCodeAndIdNot(
                request.getProductCode(), id)) {

            throw new InsuranceProductAlreadyExistsException(
                    "Another product already uses code: "
                            + request.getProductCode());
        }

        product.setProductCode(request.getProductCode());
        product.setProductName(request.getProductName());
        product.setInsuranceType(request.getInsuranceType());
        product.setDescription(request.getDescription());
        product.setCoverageAmount(request.getCoverageAmount());
        product.setPremiumAmount(request.getPremiumAmount());
        product.setPaymentFrequency(request.getPaymentFrequency());
        product.setPolicyTermInYears(request.getPolicyTermInYears());

        InsuranceProduct updatedProduct =
                productRepository.save(product);

        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @CacheEvict(cacheNames = "insuranceProducts", allEntries = true)
    public InsuranceProductResponse deactivateProduct(Long id) {

        InsuranceProduct product = findProductOrThrow(id);

        product.setActive(false);

        InsuranceProduct savedProduct =
                productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InsuranceProductResponse> getAllProducts(
            int page,
            int size,
            String sortBy,
            String direction) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100");
        }

        if (sortBy == null
                || !sortBy.matches("[a-zA-Z][a-zA-Z0-9]*")) {

            throw new IllegalArgumentException(
                    "Invalid sorting field");
        }

        Sort.Direction sortDirection;

        try {
            sortDirection = Sort.Direction.valueOf(
                    direction.toUpperCase(Locale.ROOT));
        } catch (NullPointerException | IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Sort direction must be 'asc' or 'desc'");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy));

        return productRepository.findByActiveTrue(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsuranceProductResponse> getProductsByType(
            InsuranceType insuranceType) {

        return productRepository
                .findActiveProductsByType(insuranceType)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsuranceProductResponse>
            searchProductsByCoverageAndPremium(
                    BigDecimal minimumCoverage,
                    BigDecimal maximumPremium) {

        if (minimumCoverage == null
                || minimumCoverage.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Minimum coverage must be greater than zero");
        }

        if (maximumPremium == null
                || maximumPremium.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Maximum premium must be greater than zero");
        }

        return productRepository
                .findProductsByCoverageAndPremium(
                        minimumCoverage, maximumPremium)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    private InsuranceProduct findProductOrThrow(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new InsuranceProductNotFoundException(
                                "Insurance product not found with id: " + id));
    }
}