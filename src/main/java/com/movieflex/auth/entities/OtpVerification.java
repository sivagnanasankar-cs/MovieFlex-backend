package com.movieflex.auth.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "otp_verification")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "The field name can't be blank")
    private String name;

    @NotBlank(message = "The field username can't be blank")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "The field email can't be blank")
    @Column(unique = true)
    @Email(message = "Please enter email in proper format!")
    private String email;

    @NotBlank(message = "The field password can't be blank")
    @Size(min = 4, message = "The password must have at least 5 characters")
    private String password;

    @NotBlank(message = "The OTP can't be blank")
    private String otp;

    private Boolean isVerified;

    private Instant expiration; // OTP expiration date

    private Instant createdAt; // Timestamp when the OTP was created
}
