package com.movieflex.controller;

import com.movieflex.auth.service.AuthService;
import com.movieflex.auth.service.JwtService;
import com.movieflex.auth.service.RefreshTokenService;
import com.movieflex.auth.utils.LoginRequest;
import com.movieflex.auth.utils.RefreshTokenRequest;
import com.movieflex.auth.utils.RegisterRequest;
import com.movieflex.dto.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<Response>  register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<Response>  login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Response> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        Response response = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<Response> verifyOtp(@PathVariable String otp, @PathVariable String email) {
        Response response = authService.verifyRegistration(otp, email);
        return ResponseEntity.ok(response);
    }

}
