package com.example.insurance.exception;

public class InsuranceProductNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InsuranceProductNotFoundException(String message) {
        super(message);
    }
}