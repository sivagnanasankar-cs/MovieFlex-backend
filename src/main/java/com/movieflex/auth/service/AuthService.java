package com.movieflex.auth.service;

import com.movieflex.auth.entities.OtpVerification;
import com.movieflex.auth.entities.RefreshToken;
import com.movieflex.auth.entities.User;
import com.movieflex.auth.entities.UserRole;
import com.movieflex.auth.repositories.OtpVerificationRepository;
import com.movieflex.auth.repositories.UserRepository;
import com.movieflex.auth.utils.AuthResponse;
import com.movieflex.auth.utils.LoginRequest;
import com.movieflex.auth.utils.RegisterRequest;
import com.movieflex.constants.MessageCodes;
import com.movieflex.dto.MailBody;
import com.movieflex.dto.Response;
import com.movieflex.service.EmailService;
import com.movieflex.utils.CommonUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final OtpVerificationRepository otpVerificationRepository;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService, RefreshTokenService refreshTokenService, AuthenticationManager authenticationManager, EmailService emailService, OtpVerificationRepository otpVerificationRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.otpVerificationRepository = otpVerificationRepository;
    }

    public Response register(RegisterRequest registerRequest){
        CommonUtils.validateRegisterRequest(registerRequest);
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            return Response.builder()
                    .statusCode("409")
                    .statusDescription("Email is already registered")
                    .build();
        }
        if(userRepository.existsByUsername(registerRequest.getUsername())){
            return Response.builder()
                    .statusCode("409")
                    .statusDescription("Username is already exists")
                    .build();
        }

        OtpVerification otpVerification = this.createOtpVerification(registerRequest);
        otpVerification = otpVerificationRepository.save(otpVerification);
        this.sendEmailInBackground(this.createMailBodyForOtpVerification(otpVerification));

        return Response.builder()
                .statusCode(MessageCodes.OK)
                .statusDescription("Verification OTP sent successfully")
                .build();
    }

    private OtpVerification createOtpVerification(RegisterRequest registerRequest) {
        return OtpVerification.builder()
                .email(registerRequest.getEmail())
                .name(registerRequest.getName())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .otp(this.otpGenerator())
                .isVerified(Boolean.FALSE)
                .expiration(Instant.now().plusSeconds(5 * 60))
                .createdAt(Instant.now())
                .build();
    }

    private void sendEmailInBackground(MailBody mailBody){
        CompletableFuture.runAsync(() -> {
            emailService.sendSimpleMessage(mailBody);
        });
    }

    private MailBody createMailBodyForOtpVerification(OtpVerification otpVerification){
        return MailBody.builder()
                .to(otpVerification.getEmail())
                .subject("Registration OTP from MovieFlex")
                .text("Hi,\n\n" + otpVerification.getOtp() + " is your MovieFlex verification OTP. Please do not share it with anyone")
                .build();
    }

    private MailBody createMailBodyForRegisteration(User user){
        return MailBody.builder()
                .to(user.getEmail())
                .subject("Welcome To MovieFlex")
                .text("Hi "+ user.getName() +",\nYour account is registered successfully")
                .build();
    }



    public Response verifyRegistration(String otp, String email){
        boolean emailExists = otpVerificationRepository.existsByEmail(email);
        if(!emailExists){
            return  Response.builder()
                    .statusCode(MessageCodes.INTERNAL_SERVER_ERROR)
                    .statusDescription("Email not found for Verification. Please Verify your email id " + email )
                    .build();
        }
        OtpVerification otpVerification = otpVerificationRepository.findByEmailAndOtp(email, otp).orElse(null);
        if(CommonUtils.checkIsNullOrEmpty(otpVerification)){
            return Response.builder()
                    .statusCode(MessageCodes.INTERNAL_SERVER_ERROR)
                    .statusDescription("Entered otp is wrong.\nTry again")
                    .build();
        }
        if(otpVerification.getIsVerified()){
            return Response.builder()
                    .statusCode(MessageCodes.INTERNAL_SERVER_ERROR)
                    .statusDescription("Entered otp is Verified. Try Logging in.")
                    .build();
        }
        if(otpVerification.getExpiration().isBefore(Instant.now())){
            otpVerificationRepository.deleteById(otpVerification.getId());
            return Response.builder()
                    .statusCode(MessageCodes.INTERNAL_SERVER_ERROR)
                    .statusDescription("Entered otp is Expired.\nGenerate new OTP")
                    .build();
        }

        User user = this.createUser(otpVerification);
        otpVerification.setIsVerified(Boolean.TRUE);
        otpVerificationRepository.save(otpVerification);
        this.sendEmailInBackground(this.createMailBodyForRegisteration(user));
        User savedUser = userRepository.save(user);
        String accessToken = jwtService.generateToken(savedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
        return Response.builder()
                .statusCode(MessageCodes.OK)
                .statusDescription("Registration Successful")
                .data(authResponse)
                .build();
    }

    private User createUser(OtpVerification otpVerification){
        return User.builder()
                .name(otpVerification.getName())
                .email(otpVerification.getEmail())
                .username(otpVerification.getUsername())
                .password(otpVerification.getPassword())
                .role(UserRole.USER)
                .build();
    }

    public Response login(LoginRequest loginRequest){
        CommonUtils.isValidEmailFormat(loginRequest.getEmail());
        if(CommonUtils.checkIsNullOrEmpty(loginRequest.getPassword())){
            throw new IllegalArgumentException("Empty password");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        var user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("user not found!"));
        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
        return Response.builder()
                .statusCode(MessageCodes.OK)
                .statusDescription("Login Successful")
                .data(authResponse)
                .build();
    }

    private String otpGenerator() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}