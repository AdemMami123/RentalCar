package com.carrental.modules.auth.service;

import com.carrental.modules.auth.dto.AuthResponse;
import com.carrental.modules.auth.dto.LoginRequest;
import com.carrental.modules.auth.dto.RefreshRequest;
import com.carrental.modules.auth.dto.RegisterRequest;
import com.carrental.modules.auth.dto.UserResponse;
import com.carrental.modules.auth.entity.RefreshToken;
import com.carrental.modules.auth.repository.RefreshTokenRepository;
import com.carrental.modules.user.entity.Role;
import com.carrental.modules.user.entity.User;
import com.carrental.modules.user.repository.RoleRepository;
import com.carrental.modules.user.repository.UserRepository;
import com.carrental.security.JwtService;
import com.carrental.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Value("${jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.getEmail());
        if (userRepository.existsByEmail(email)) throw new IllegalStateException("Email is already registered");
        Role userRole = roleRepository.findByName("USER").orElseThrow(() -> new ResourceNotFoundException("Default USER role is missing"));
        User user = User.builder().email(email).password(passwordEncoder.encode(request.getPassword())).firstName(request.getFirstName()).lastName(request.getLastName()).roles(new java.util.HashSet<>(java.util.Set.of(userRole))).build();
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());
        try { authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword())); }
        catch (BadCredentialsException exception) { throw new BadCredentialsException("Invalid email or password"); }
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        return issueTokens(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Long userId) { return UserResponse.from(userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"))); }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash(request.getRefreshToken())).orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        if (stored.isRevoked() || stored.getExpiresAt().isBefore(LocalDateTime.now())) throw new BadCredentialsException("Refresh token is expired or revoked");
        stored.setRevoked(true); stored.setRevokedAt(LocalDateTime.now()); refreshTokenRepository.save(stored);
        return issueTokens(stored.getUser());
    }

    @Transactional
    public void logout(RefreshRequest request) { refreshTokenRepository.findByTokenHash(hash(request.getRefreshToken())).ifPresent(token -> { token.setRevoked(true); token.setRevokedAt(LocalDateTime.now()); }); }

    private AuthResponse issueTokens(User user) {
        String rawRefreshToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes(48));
        RefreshToken refreshToken = new RefreshToken(); refreshToken.setUser(user); refreshToken.setTokenHash(hash(rawRefreshToken)); refreshToken.setCreatedAt(LocalDateTime.now()); refreshToken.setExpiresAt(LocalDateTime.now().plusNanos(refreshTokenExpirationMs * 1_000_000)); refreshToken.setRevoked(false); refreshTokenRepository.save(refreshToken);
        return AuthResponse.builder().accessToken(jwtService.generateAccessToken(user)).refreshToken(rawRefreshToken).tokenType("Bearer").expiresIn(jwtService.getAccessTokenExpirationMs() / 1000).user(UserResponse.from(user)).build();
    }

    private String normalizeEmail(String email) { return email.trim().toLowerCase(java.util.Locale.ROOT); }
    private byte[] randomBytes(int size) { byte[] bytes = new byte[size]; new SecureRandom().nextBytes(bytes); return bytes; }
    private String hash(String value) { try { return java.util.HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))); } catch (NoSuchAlgorithmException exception) { throw new IllegalStateException("Hash algorithm unavailable", exception); } }
}