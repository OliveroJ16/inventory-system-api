package com.inventory.inventorySystem.controller;

import com.inventory.inventorySystem.dto.OnCreate;
import com.inventory.inventorySystem.dto.OnUpdate;
import com.inventory.inventorySystem.dto.request.CustomerRequest;
import com.inventory.inventorySystem.dto.response.CustomerResponse;
import com.inventory.inventorySystem.dto.response.PaginatedResponse;
import com.inventory.inventorySystem.service.interfaces.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
@Tag(
        name = "Customers",
        description = "Customer management"
)
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    @Operation(
            summary = "Register customer",
            description = "Registers a new customer. Accessible by ADMIN and CASHIER roles."
    )
    public ResponseEntity<CustomerResponse> registerCustomer(@Validated(OnCreate.class) @RequestBody CustomerRequest customerRequest){
        CustomerResponse customerResponse = customerService.registerCustomer(customerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerResponse);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update customer",
            description = "Updates an existing customer by ID. Requires ADMIN role."
    )
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable UUID id, @Validated(OnUpdate.class) @RequestBody CustomerRequest customerRequest){
        CustomerResponse customerResponse = customerService.updateCustomer(id, customerRequest);
        return ResponseEntity.status(HttpStatus.OK).body(customerResponse);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    @Operation(
            summary = "Get customers",
            description = "Returns a paginated list of customers. Accessible by ADMIN and CASHIER roles."
    )
    public ResponseEntity<PaginatedResponse<CustomerResponse>> getAllCustomers(@RequestParam(required = false) String name, @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable){
        PaginatedResponse<CustomerResponse> customerResponse = customerService.getAllCustomers(name, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(customerResponse);
    }
}
