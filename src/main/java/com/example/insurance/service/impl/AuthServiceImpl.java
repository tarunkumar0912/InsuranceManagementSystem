package com.example.insurance.service.impl;

import com.example.insurance.dto.request.OtpRequest;
import com.example.insurance.dto.request.OtpVerificationRequest;
import com.example.insurance.dto.request.UserRegistrationRequest;
import com.example.insurance.dto.response.AuthResponse;
import com.example.insurance.entity.OtpVerification;
import com.example.insurance.entity.UserAccount;
import com.example.insurance.enums.UserRole;
import com.example.insurance.repository.OtpVerificationRepository;
import com.example.insurance.repository.UserAccountRepository;
import com.example.insurance.security.JwtService;
import com.example.insurance.service.AuthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserAccountRepository userRepository;
    private final OtpVerificationRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SecureRandom secureRandom = new SecureRandom();

    private final int otpExpirationMinutes;
    private final int maxOtpAttempts;
    private final boolean logOtpToConsole;

    public AuthServiceImpl(
            UserAccountRepository userRepository,
            OtpVerificationRepository otpRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${app.otp.expiration-minutes:5}")
            int otpExpirationMinutes,
            @Value("${app.otp.max-attempts:5}")
            int maxOtpAttempts,
            @Value("${app.otp.log-to-console:false}")
            boolean logOtpToConsole) {

        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.otpExpirationMinutes = otpExpirationMinutes;
        this.maxOtpAttempts = maxOtpAttempts;
        this.logOtpToConsole = logOtpToConsole;
    }

    @Override
    @Transactional
    public void register(UserRegistrationRequest request) {

        String email = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException(
                    "An account with this email already exists"
            );
        }

        UserAccount account = new UserAccount();
        account.setFullName(request.getFullName().trim());
        account.setEmail(email);
        account.setMobileNumber(request.getMobileNumber());
        account.setRole(UserRole.CUSTOMER);
        account.setEnabled(true);

        userRepository.save(account);
    }

    @Override
    @Transactional
    public void requestOtp(OtpRequest request) {

        String email = normalizeEmail(request.getEmail());

        UserAccount account = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "If the account is eligible, an OTP will be issued"
                ));

        if (!account.isEnabled()) {
            throw new IllegalArgumentException(
                    "If the account is eligible, an OTP will be issued"
            );
        }

        // Invalidate previous OTPs for this email.
        otpRepository.deleteByEmail(email);

        String otp = String.format(
                "%06d",
                secureRandom.nextInt(1_000_000)
        );

        OtpVerification verification = new OtpVerification();
        verification.setEmail(email);
        verification.setOtpHash(passwordEncoder.encode(otp));
        verification.setCreatedAt(LocalDateTime.now());
        verification.setExpiresAt(
                LocalDateTime.now().plusMinutes(otpExpirationMinutes)
        );
        verification.setAttempts(0);
        verification.setConsumed(false);

        otpRepository.save(verification);

        if (logOtpToConsole) {
            logger.warn(
                    "DEVELOPMENT ONLY - OTP for {} is {}. Do not enable OTP logging in production.",
                    email,
                    otp
            );
        } else {
            logger.info("OTP request processed for {}", email);
        }
    }

    @Override
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public AuthResponse verifyOtp(OtpVerificationRequest request) {

        String email = normalizeEmail(request.getEmail());

        OtpVerification verification = otpRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid or expired OTP"
                ));

        if (verification.isConsumed()) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            verification.setConsumed(true);
            otpRepository.save(verification);
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        if (verification.getAttempts() >= maxOtpAttempts) {
            verification.setConsumed(true);
            otpRepository.save(verification);
            throw new IllegalArgumentException(
                    "Maximum OTP attempts exceeded. Request a new OTP."
            );
        }

        if (!passwordEncoder.matches(
                request.getOtp(),
                verification.getOtpHash())) {

            verification.setAttempts(verification.getAttempts() + 1);
            if (verification.getAttempts() >= maxOtpAttempts) {
                verification.setConsumed(true);
            }
            otpRepository.save(verification);

            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        UserAccount account = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User account not found"
                ));

        if (!account.isEnabled()) {
            throw new IllegalArgumentException("User account is disabled");
        }

        verification.setConsumed(true);
        otpRepository.save(verification);

        String token = jwtService.generateToken(
                account.getEmail(),
                account.getRole().name()
        );

        logger.info("Authentication successful for user ID {}",
                account.getId());

        return new AuthResponse(
                "Authentication successful",
                token,
                jwtService.getExpirationMs() / 1000,
                account.getId(),
                account.getEmail(),
                account.getRole().name()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}