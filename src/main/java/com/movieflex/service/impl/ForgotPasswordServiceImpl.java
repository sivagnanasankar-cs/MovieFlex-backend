package com.movieflex.service.impl;

import com.movieflex.auth.entities.ForgotPassword;
import com.movieflex.auth.entities.User;
import com.movieflex.auth.repositories.ForgotPasswordRepository;
import com.movieflex.auth.repositories.UserRepository;
import com.movieflex.auth.utils.ChangePassword;
import com.movieflex.constants.MessageCodes;
import com.movieflex.dto.MailBody;
import com.movieflex.dto.Response;
import com.movieflex.service.EmailService;
import com.movieflex.service.ForgotPasswordService;
import com.movieflex.utils.CommonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Random;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordServiceImpl(UserRepository userRepository, EmailService emailService, ForgotPasswordRepository forgotPasswordRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Response verifyMail(String email) {
        if(CommonUtils.checkIsNullOrEmpty(email)){
            throw new IllegalArgumentException("Email is empty");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email"));

        int otp = this.otpGenerator();

        MailBody mailBody = MailBody.builder()
                .to(email)
                .text(otp + " is the otp for your forgot password request. It will expire in 5 minute")
                .subject("OTP for Forgot Password Request")
                .build();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(Date.from(Instant.now().plusSeconds((10 * 60))))
                .user(user)
                .build();


        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(fp);
        return Response.builder()
                .statusCode(MessageCodes.OK)
                .statusDescription("OTP sent successfully")
                .data("Email sent for verification!")
                .build();

    }

    @Override
    public Response verifyOtp(Integer otp, String email) {
        if(CommonUtils.checkIsNullOrEmpty(email)){
            throw new IllegalArgumentException("Email is empty");
        }
        if(otp == null){
            throw new IllegalArgumentException("OTP is empty");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email"));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new RuntimeException("Invalid otp for email" + email));

        if(fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepository.deleteById(fp.getFpid());
            return Response.builder()
                    .statusCode(MessageCodes.INTERNAL_SERVER_ERROR)
                    .statusDescription("Time Excedeed")
                    .data("OTP validity Expired - Try again")
                    .build();
        }

        return Response.builder()
                .statusCode(MessageCodes.OK)
                .statusDescription("OTP verified Successfully")
                .data("OTP verified")
                .build();
    }

    @Override
    public Response changePassword(String email, ChangePassword changePassword) {
        Response response = new Response();
        if (!changePassword.getPassword().equals(changePassword.getRepeatPassword())) {
            response.setStatusCode(HttpStatus.EXPECTATION_FAILED.toString());
            response.setStatusCode("\"Passwords do not match!\n Please re enter");
            response.setData(null);
            return response;
        }
        String encodedPassword = passwordEncoder.encode(changePassword.getPassword());
        userRepository.updatePassword(email, encodedPassword);
        response.setStatusCode(HttpStatus.OK.toString());
        response.setStatusDescription(HttpStatus.OK.getReasonPhrase());
        response.setData("Password has been changed");
        return response;
    }

    private int otpGenerator() {
        return 100000 + new Random().nextInt(900000);
    }
}
