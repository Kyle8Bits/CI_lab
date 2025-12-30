package com.kyle.template.todo.services;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kyle.template.todo.config.JweConfig;
import com.kyle.template.todo.exceptions.InvalidTokenException;
import com.kyle.template.todo.internal_api.dto.TokenInternalDto;
import com.kyle.template.todo.internal_api.interfaces.TokenApi;
import com.kyle.template.todo.model.entity.User;
import com.kyle.template.todo.repository.UserRepository;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class TokenServiceImpl implements TokenApi {

    private final UserRepository userRepository;
    private final JweConfig jweConfig;

    @Override
    public TokenInternalDto generateTokens(String userId, String username, String ipAddress, String deviceInfo) {
        LocalDateTime now = LocalDateTime.now();

        // Generate encrypted JWE access token (2.2.1)
        Date accessTokenExpiry = new Date(
                System.currentTimeMillis() + jweConfig.getAccessTokenExpirationSeconds() * 1000L
        );
        String accessToken = generateJweToken(userId, username, accessTokenExpiry);

        // Generate refresh token (random UUID, stored in database)
        String refreshTokenValue = UUID.randomUUID().toString();
        LocalDateTime refreshTokenExpiry = now.plusSeconds(
                jweConfig.getRefreshTokenExpirationSeconds()
        );
        // Get user entity for refresh token
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidTokenException("User not found"));

        log.debug("Generated JWE tokens for user: {}", username);

        return TokenInternalDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .accessTokenExpiry(LocalDateTime.now().plusSeconds(jweConfig.getAccessTokenExpirationSeconds()))
                .refreshTokenExpiry(refreshTokenExpiry)
                .userId(userId)
                .username(username)
                .build();
    }

    @Override
    public String extractUserIdFromAccessToken(String token) {
        try {
            EncryptedJWT encryptedJWT = EncryptedJWT.parse(token);
            DirectDecrypter decrypter = new DirectDecrypter(jweConfig.getEncryptionKey());
            encryptedJWT.decrypt(decrypter);

            JWTClaimsSet claims = encryptedJWT.getJWTClaimsSet();
            String userId = claims.getStringClaim("userId");
            if (userId == null || userId.isEmpty()) {
                throw new InvalidTokenException("userId missing in token");
            }
            return userId;
        } catch (ParseException | JOSEException e) {
            log.error("Failed to parse/decrypt token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token");
        }
    }

    //Helpers
    private String generateJweToken(String userId, String username, Date expiry) {
        try {
            // Build JWE claims (payload)
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .claim("userId", userId)
                    .claim("username", username)
                    .issueTime(new Date())
                    .expirationTime(expiry)
                    .jwtID(UUID.randomUUID().toString()) // Unique token ID for revocation
                    .build();

            // Create JWE header with direct encryption using A256GCM
            JWEHeader header = new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
                    .contentType("JWT")
                    .build();

            // Create encrypted JWE
            EncryptedJWT encryptedJWT = new EncryptedJWT(header, claimsSet);

            // Encrypt with AES-256 key
            DirectEncrypter encrypter = new DirectEncrypter(jweConfig.getEncryptionKey());
            encryptedJWT.encrypt(encrypter);

            // Serialize to compact form
            return encryptedJWT.serialize();

        } catch (JOSEException e) {
            log.error("Failed to generate JWE token: {}", e.getMessage());
            throw new RuntimeException("Failed to generate token", e);
        }
    }

}
