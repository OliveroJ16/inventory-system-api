package com.inventory.inventorySystem.controller;

import com.inventory.inventorySystem.dto.request.SaleRequest;
import com.inventory.inventorySystem.dto.response.CompleteSaleResponse;
import com.inventory.inventorySystem.dto.response.PaginatedResponse;
import com.inventory.inventorySystem.dto.response.SaleResponse;
import com.inventory.inventorySystem.service.interfaces.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sales")
@Tag(
        name = "Sales",
        description = "Sales registration, queries and payment management"
)

public class SaleController {

    private final SaleService saleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    @Operation(
            summary = "Register sale",
            description = "Registers a new sale. Accessible by ADMIN and CASHIER roles."
    )
    public ResponseEntity<CompleteSaleResponse> registerSale(@RequestBody  SaleRequest saleRequest){
        CompleteSaleResponse completeSaleResponse = saleService.registerSale(saleRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(completeSaleResponse);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Get sales",
            description = "Returns a paginated list of sales filtered by date range or customer name. Requires ADMIN role."
    )
    public ResponseEntity<PaginatedResponse<SaleResponse>> getAllSales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String customerName,
            @PageableDefault(page = 0, size = 10, sort = "date",direction = Sort.Direction.ASC)Pageable pageable){
        PaginatedResponse<SaleResponse> saleResponses = saleService.getAllSales(startDate, endDate, customerName, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(saleResponses);
    }
}
