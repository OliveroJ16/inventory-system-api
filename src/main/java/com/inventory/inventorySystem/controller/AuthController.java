package com.inventory.inventorySystem.controller;

import com.inventory.inventorySystem.dto.request.LoginRequest;
import com.inventory.inventorySystem.dto.request.RegisterRequest;
import com.inventory.inventorySystem.dto.response.UserResponse;
import com.inventory.inventorySystem.exceptions.InvalidTokenException;
import com.inventory.inventorySystem.security.CookieProvider;
import com.inventory.inventorySystem.service.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(
        name = "Authentication",
        description = "Authentication and token management"
)
public class AuthController {

    private final AuthService authService;
    private final CookieProvider cookieProvider;

    @PostMapping("/register")
    @Operation(
            summary = "Register user",
            description = "Registers a new user and returns an access token and refresh token."
    )
    public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid RegisterRequest registerRequest) {
        var authResponse = authService.registerUser(registerRequest);
        ResponseCookie refreshTokenCookie = cookieProvider.createRefreshTokenCookie(authResponse.refreshToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authResponse.accessToken())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(authResponse.user());
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Authenticates a user and returns an access token and refresh token."
    )
    public ResponseEntity<UserResponse> loginUser(@RequestBody @Valid LoginRequest loginRequest){
        var authResponse = authService.loginUser(loginRequest);
        ResponseCookie refreshTokenCookie = cookieProvider.createRefreshTokenCookie(authResponse.refreshToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authResponse.accessToken())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(authResponse.user());
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Generates a new access token using a valid refresh token stored in cookies."
    )
    public ResponseEntity<UserResponse> refreshToken(HttpServletRequest request) {
        String refreshToken = cookieProvider.getRefreshTokenFromCookie(request)
                .orElseThrow(() -> new InvalidTokenException("Refresh token is missing or invalid"));
        var authResponse = authService.refreshToken(refreshToken);
        ResponseCookie refreshTokenCookie = cookieProvider.createRefreshTokenCookie(authResponse.refreshToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authResponse.accessToken())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(authResponse.user());
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout user",
            description = "Invalidates the refresh token and clears authentication cookies."
    )
    public ResponseEntity<Void> logoutUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        authService.logout(authHeader);
        ResponseCookie deletedCookie = cookieProvider.getDeletedRefreshTokenCookie();
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .header(HttpHeaders.SET_COOKIE, deletedCookie.toString())
                .build();
    }
}
