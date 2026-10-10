package com.example.insurance.service;

import org.springframework.data.domain.Page;

import com.example.insurance.dto.request.CustomerRegistrationRequest;
import com.example.insurance.dto.response.CustomerResponse;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRegistrationRequest request);

    CustomerResponse getCustomerById(Long id);

    CustomerResponse updateCustomer(
            Long id,
            CustomerRegistrationRequest request);

    void deleteCustomer(Long id);

    Page<CustomerResponse> getAllCustomers(
            int page,
            int size,
            String sortBy,
            String direction);
}