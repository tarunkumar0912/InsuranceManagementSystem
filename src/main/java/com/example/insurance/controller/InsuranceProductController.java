package com.example.insurance.controller;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.insurance.dto.request.InsuranceProductRequest;
import com.example.insurance.dto.response.InsuranceProductResponse;
import com.example.insurance.enums.InsuranceType;
import com.example.insurance.service.InsuranceProductService;

@RestController
@RequestMapping("/api/products")
public class InsuranceProductController {

    private final InsuranceProductService productService;

    public InsuranceProductController(
            InsuranceProductService productService) {

        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<InsuranceProductResponse> createProduct(
            @Valid @RequestBody InsuranceProductRequest request) {

        InsuranceProductResponse response =
                productService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsuranceProductResponse> getProductById(
            @PathVariable Long id) {

        InsuranceProductResponse response =
                productService.getProductById(id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsuranceProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody InsuranceProductRequest request) {

        InsuranceProductResponse response =
                productService.updateProduct(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<InsuranceProductResponse> deactivateProduct(
            @PathVariable Long id) {

        InsuranceProductResponse response =
                productService.deactivateProduct(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<InsuranceProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<InsuranceProductResponse> response =
                productService.getAllProducts(
                        page, size, sortBy, direction);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{insuranceType}")
    public ResponseEntity<List<InsuranceProductResponse>> getProductsByType(
            @PathVariable InsuranceType insuranceType) {

        List<InsuranceProductResponse> response =
                productService.getProductsByType(insuranceType);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<InsuranceProductResponse>>
            searchProductsByCoverageAndPremium(
                    @RequestParam BigDecimal minimumCoverage,
                    @RequestParam BigDecimal maximumPremium) {

        List<InsuranceProductResponse> response =
                productService.searchProductsByCoverageAndPremium(
                        minimumCoverage, maximumPremium);

        return ResponseEntity.ok(response);
    }
}