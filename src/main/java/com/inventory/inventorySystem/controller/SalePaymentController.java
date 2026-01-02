package com.inventory.inventorySystem.controller;

import com.inventory.inventorySystem.dto.OnUpdate;
import com.inventory.inventorySystem.dto.request.SalePaymentRequest;
import com.inventory.inventorySystem.dto.response.SalePaymentResponse;
import com.inventory.inventorySystem.service.interfaces.SalePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sales")
public class SalePaymentController {

    private final SalePaymentService salePaymentService;

    @PostMapping("/{saleId}/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    public ResponseEntity<SalePaymentResponse> addSalePayment(@PathVariable UUID saleId, @RequestBody @Validated(OnUpdate.class) SalePaymentRequest salePaymentRequest){
        SalePaymentResponse salePaymentResponse = salePaymentService.saveSalePayment(salePaymentRequest, saleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(salePaymentResponse);
    }
}
