package com.movieflex.auth.repositories;

import com.movieflex.auth.entities.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Integer> {
    boolean existsByEmail(String email);
    Optional<OtpVerification> findByEmailAndOtp(String email, String otp);
}
