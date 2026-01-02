package com.inventory.inventorySystem.controller;


import com.inventory.inventorySystem.dto.request.UserRequest;
import com.inventory.inventorySystem.dto.response.PaginatedResponse;
import com.inventory.inventorySystem.dto.response.UserResponse;
import com.inventory.inventorySystem.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN'")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @RequestBody UserRequest userRequest){
        UserResponse userResponse = userService.updateUser(id, userRequest);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN'")
    public ResponseEntity<PaginatedResponse<UserResponse>> getAllUsers(@PageableDefault(page = 0, size = 10, sort = "userName", direction = Sort.Direction.ASC) Pageable pageable){
        PaginatedResponse<UserResponse> userResponse = userService.getAllUsers(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }
}
