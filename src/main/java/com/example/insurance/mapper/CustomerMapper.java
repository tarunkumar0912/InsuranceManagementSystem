package com.example.insurance.mapper;

import org.springframework.stereotype.Component;

import com.example.insurance.dto.request.CustomerRegistrationRequest;
import com.example.insurance.dto.response.CustomerResponse;
import com.example.insurance.entity.Customer;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRegistrationRequest request) {

        Customer customer = new Customer();

        customer.setFullName(request.getFullName());
        customer.setMobileNumber(request.getMobileNumber());
        customer.setEmail(request.getEmail());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setPincode(request.getPincode());

        return customer;
    }

    public CustomerResponse toResponse(Customer customer) {

        CustomerResponse response = new CustomerResponse();

        response.setId(customer.getId());
        response.setFullName(customer.getFullName());
        response.setMobileNumber(customer.getMobileNumber());
        response.setEmail(customer.getEmail());
        response.setDateOfBirth(customer.getDateOfBirth());
        response.setAddress(customer.getAddress());
        response.setCity(customer.getCity());
        response.setState(customer.getState());
        response.setPincode(customer.getPincode());
        response.setCreatedDate(customer.getCreatedDate());
        response.setLastModifiedDate(customer.getLastModifiedDate());

        return response;
    }
}