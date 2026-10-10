package com.example.insurance.exception;

public class InsuranceProductAlreadyExistsException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InsuranceProductAlreadyExistsException(String message) {
        super(message);
    }
}