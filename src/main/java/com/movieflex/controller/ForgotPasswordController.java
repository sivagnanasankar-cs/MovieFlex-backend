package com.movieflex.controller;


import com.movieflex.auth.utils.ChangePassword;
import com.movieflex.dto.Response;
import com.movieflex.service.ForgotPasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    public ForgotPasswordController(ForgotPasswordService forgotPasswordService) {
        this.forgotPasswordService = forgotPasswordService;
    }

    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<Response> verifyMailHandler(@PathVariable String email) {
        Response response = forgotPasswordService.verifyMail(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<Response> verifyOtpHandler(@PathVariable Integer otp, @PathVariable String email) {
        Response response = forgotPasswordService.verifyOtp(otp, email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<Response> changePasswordHandler(@PathVariable String email,
                                                        @RequestBody ChangePassword changePassword) {
        Response response = forgotPasswordService.changePassword(email, changePassword);
        return ResponseEntity.ok(response);
    }
}

