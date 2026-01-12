package com.inventory.inventorySystem.controller;

import com.inventory.inventorySystem.dto.OnCreate;
import com.inventory.inventorySystem.dto.OnUpdate;
import com.inventory.inventorySystem.dto.request.SupplierRequest;
import com.inventory.inventorySystem.dto.response.PaginatedResponse;
import com.inventory.inventorySystem.dto.response.SupplierResponse;
import com.inventory.inventorySystem.service.interfaces.SupplierService;
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
@RequestMapping("/api/v1/suppliers")
@Tag(
        name = "Suppliers",
        description = "Supplier management"
)
public class SupplierController{

    private final SupplierService supplierService;

    @PostMapping@PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Register supplier",
            description = "Registers a new supplier. Requires ADMIN role."
    )
    public ResponseEntity<SupplierResponse> registerSupplier(@Validated(OnCreate.class) @RequestBody SupplierRequest supplierRequest){
        SupplierResponse supplierResponse = supplierService.saveSupplier(supplierRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierResponse);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Update supplier",
            description = "Updates an existing supplier by ID. Requires ADMIN role."
    )
    public ResponseEntity<SupplierResponse> updateSupplier(@PathVariable UUID id, @Validated(OnUpdate.class) @RequestBody SupplierRequest supplierRequest){
        SupplierResponse supplierResponse = supplierService.updateSupplier(id, supplierRequest);
        return ResponseEntity.status(HttpStatus.OK).body(supplierResponse);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Get suppliers",
            description = "Returns a paginated list of suppliers. Requires ADMIN role."
    )
    public ResponseEntity<PaginatedResponse<SupplierResponse>> getAllSupplier(@PageableDefault(page = 0, size = 10, sort = "full_name", direction = Sort.Direction.ASC) Pageable pageable){
        PaginatedResponse<SupplierResponse> supplierResponse = supplierService.getAllSupplier(pageable);
        return  ResponseEntity.status(HttpStatus.OK).body(supplierResponse);
    }
}
