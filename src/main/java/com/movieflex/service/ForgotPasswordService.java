package com.movieflex.service;

import com.movieflex.auth.utils.ChangePassword;
import com.movieflex.dto.Response;

public interface ForgotPasswordService {

    Response verifyMail(String email);

    Response verifyOtp(Integer otp, String email);

    Response changePassword(String email, ChangePassword changePassword);
}
