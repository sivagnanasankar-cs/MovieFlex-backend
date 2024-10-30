package com.movieflex.auth.service;

import com.movieflex.auth.entities.RefreshToken;
import com.movieflex.auth.entities.User;
import com.movieflex.auth.repositories.RefreshTokenRepository;
import com.movieflex.auth.repositories.UserRepository;
import com.movieflex.auth.utils.AuthResponse;
import com.movieflex.constants.MessageCodes;
import com.movieflex.dto.Response;
import com.movieflex.utils.CommonUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    public RefreshToken createRefreshToken(String username){
        User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        RefreshToken refreshToken = user.getRefereshToken();

        if(refreshToken == null){
            final long refreshTokenValidity = 5 * 60 * 60 * 10000;
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expiration(Instant.now().plusMillis(refreshTokenValidity))
                    .user(user)
                    .build();
            refreshTokenRepository.save(refreshToken);
        }

        return refreshToken;
    }

    public Response verifyRefreshToken(String refreshToken){

        RefreshToken refToken =  refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found!"));

        if(CommonUtils.checkIsNullOrEmpty(refreshToken)){
            return  Response.builder()
                    .statusCode(MessageCodes.INTERNAL_SERVER_ERROR)
                    .statusDescription("Refresh token not found!")
                    .data(null)
                    .build();
        }

        if(refToken.getExpiration().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(refToken);
            return Response.builder()
                    .statusCode(MessageCodes.INTERNAL_SERVER_ERROR)
                    .statusDescription("Refresh token expired!")
                    .data(null)
                    .build();
        }

        String accessToken = jwtService.generateToken(refToken.getUser());
        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refToken.getRefreshToken())
                .build();

        return Response.builder()
                .statusCode(MessageCodes.OK)
                .statusDescription("Refresh token valid!")
                .data(authResponse)
                .build();
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void deleteExpiredTokens() {
        Instant now = Instant.now();
        refreshTokenRepository.deleteByExpirationBefore(now);
    }
}
