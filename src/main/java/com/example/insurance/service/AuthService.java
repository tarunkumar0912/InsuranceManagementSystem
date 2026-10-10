package com.example.insurance.service;

import com.example.insurance.dto.request.OtpRequest;
import com.example.insurance.dto.request.OtpVerificationRequest;
import com.example.insurance.dto.request.UserRegistrationRequest;
import com.example.insurance.dto.response.AuthResponse;

public interface AuthService {

    void register(UserRegistrationRequest request);

    void requestOtp(OtpRequest request);

    AuthResponse verifyOtp(OtpVerificationRequest request);
}