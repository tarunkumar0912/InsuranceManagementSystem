package com.example.insurance.service.impl;

import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.insurance.dto.request.CustomerRegistrationRequest;
import com.example.insurance.dto.response.CustomerResponse;
import com.example.insurance.entity.Customer;
import com.example.insurance.exception.CustomerAlreadyExistsException;
import com.example.insurance.exception.CustomerNotFoundException;
import com.example.insurance.mapper.CustomerMapper;
import com.example.insurance.repository.CustomerRepository;
import com.example.insurance.service.CustomerService;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(
            CustomerRepository customerRepository,
            CustomerMapper customerMapper) {

        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public CustomerResponse createCustomer(
            CustomerRegistrationRequest request) {

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomerAlreadyExistsException(
                    "A customer with this email already exists");
        }

        if (customerRepository.existsByMobileNumber(
                request.getMobileNumber())) {

            throw new CustomerAlreadyExistsException(
                    "A customer with this mobile number already exists");
        }

        Customer customer = customerMapper.toEntity(request);

        Customer savedCustomer = customerRepository.save(customer);

        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {

        Customer customer = findCustomerOrThrow(id);

        return customerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse updateCustomer(
            Long id,
            CustomerRegistrationRequest request) {

        Customer customer = findCustomerOrThrow(id);

        if (customerRepository.existsByEmailAndIdNot(
                request.getEmail(), id)) {

            throw new CustomerAlreadyExistsException(
                    "Another customer is already registered with this email");
        }

        if (customerRepository.existsByMobileNumberAndIdNot(
                request.getMobileNumber(), id)) {

            throw new CustomerAlreadyExistsException(
                    "Another customer is already registered with this mobile number");
        }

        customer.setFullName(request.getFullName());
        customer.setMobileNumber(request.getMobileNumber());
        customer.setEmail(request.getEmail());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setPincode(request.getPincode());

        Customer updatedCustomer = customerRepository.save(customer);

        return customerMapper.toResponse(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {

        Customer customer = findCustomerOrThrow(id);

        customerRepository.delete(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAllCustomers(
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

        return customerRepository.findAll(pageable)
                .map(customerMapper::toResponse);
    }

    private Customer findCustomerOrThrow(Long id) {

        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with id: " + id));
    }
}