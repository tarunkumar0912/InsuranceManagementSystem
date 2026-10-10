package com.example.insurance.repository;

import com.example.insurance.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpVerificationRepository
        extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification>
        findTopByEmailOrderByCreatedAtDesc(String email);

    void deleteByEmail(String email);
}