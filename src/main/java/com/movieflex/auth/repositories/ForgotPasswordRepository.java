package com.movieflex.auth.repositories;

import com.movieflex.auth.entities.ForgotPassword;
import com.movieflex.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {

    @Query("select fp from ForgotPassword fp where fp.otp = ?1 and  fp.user.email = ?2")
    Optional<ForgotPassword> findByOtpAndUser(Integer otp, User user);
}
