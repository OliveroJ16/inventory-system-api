package com.inventory.inventorySystem.service;

import com.inventory.inventorySystem.dto.request.LoginRequest;
import com.inventory.inventorySystem.dto.request.RegisterRequest;
import com.inventory.inventorySystem.dto.response.AuthResponse;
import com.inventory.inventorySystem.dto.response.UserResponse;
import com.inventory.inventorySystem.enums.TokenType;
import com.inventory.inventorySystem.exceptions.InvalidTokenException;
import com.inventory.inventorySystem.exceptions.ResourceNotFoundException;
import com.inventory.inventorySystem.mapper.interfaces.UserMapper;
import com.inventory.inventorySystem.model.Token;
import com.inventory.inventorySystem.model.User;
import com.inventory.inventorySystem.repository.TokenRepository;
import com.inventory.inventorySystem.repository.UserRepository;
import com.inventory.inventorySystem.security.JwtTokenProvider;
import com.inventory.inventorySystem.service.interfaces.AuthService;
import com.inventory.inventorySystem.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    public AuthResponse registerUser(RegisterRequest registerRequest){
        var userResponse = userService.saveUser(registerRequest);
        String accessToken = jwtTokenProvider.generateAccessToken(userResponse);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userResponse);
        saveToken(userResponse, refreshToken);
        return new AuthResponse(accessToken, refreshToken, userResponse);
    }

    @Override
    public AuthResponse loginUser(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Authentication failed"));
        var userResponse = userMapper.toDto(user);
        String accessToken = jwtTokenProvider.generateAccessToken(userResponse);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userResponse);
        Token token = tokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new InsufficientAuthenticationException("Authentication failed"));
        token.setRevoked(false);
        token.setExpired(false);
        token.setRefreshToken(refreshToken);
        tokenRepository.save(token);
        return new AuthResponse(accessToken, refreshToken, userResponse);
    }

    private void saveToken(UserResponse userResponse, String refreshToken){
        var user = userRepository.findById(userResponse.id())
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userResponse.id()));
        var token = Token
                .builder()
                .refreshToken(refreshToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .user(user).build();
        tokenRepository.save(token);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        String userEmail = jwtTokenProvider.getUsernameFromToken(refreshToken);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        if (!jwtTokenProvider.isRefreshTokenValid(refreshToken, user)) {
            throw new InvalidTokenException("Refresh token is invalid or expired");
        }

        Token storedToken = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("Token", "Refresh Token", "Token invalid"));

        if (storedToken.getRevoked() || storedToken.getExpired()) {
            throw new InvalidTokenException("Refresh token is invalid or expired");
        }

        var userResponse = userMapper.toDto(user);
        String newAccessToken = jwtTokenProvider.generateAccessToken(userResponse);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userResponse);

        updateStoredToken(storedToken, newRefreshToken);

        return new AuthResponse(newAccessToken, newRefreshToken, userResponse);
    }

    private String extractToken(String authHeader) {
        final String BEARER_PREFIX = "Bearer ";
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new InvalidTokenException("Invalid refresh token: missing or incorrect prefix");
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            throw new InvalidTokenException("Invalid refresh token: token is empty");
        }
        return token;
    }

    private void updateStoredToken(Token token, String newRefreshToken) {
        token.setRefreshToken(newRefreshToken);
        token.setRevoked(false);
        token.setExpired(false);
        tokenRepository.save(token);
    }

    @Override
    public void logout(String authHeader) {
        String accessToken = extractToken(authHeader);
        String userEmail = jwtTokenProvider.getUsernameFromToken(accessToken);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        if (!jwtTokenProvider.isAccessTokenValid(accessToken, user)) {
            throw new InvalidTokenException("Refresh token is invalid or expired");
        }
        Token token = tokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Token", "userId", user.getId()));
        token.setExpired(true);
        token.setRevoked(true);
        tokenRepository.save(token);
    }
}
