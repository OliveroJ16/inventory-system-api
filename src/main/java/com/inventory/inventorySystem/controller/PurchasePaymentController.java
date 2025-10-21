package com.inventory.inventorySystem.controller;

import com.inventory.inventorySystem.dto.request.PurchasePaymentRequest;
import com.inventory.inventorySystem.dto.response.PurchasePaymentResponse;
import com.inventory.inventorySystem.service.interfaces.PurchasePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/purchases")
public class PurchasePaymentController {

    private final PurchasePaymentService purchasePaymentService;

    @PostMapping("/{purchaseId}/payments")
    public ResponseEntity<PurchasePaymentResponse> registerPurchasePayment(@PathVariable UUID purchaseId, @RequestBody PurchasePaymentRequest purchasePaymentRequest){
        PurchasePaymentResponse purchasePaymentResponse = purchasePaymentService.savePurchasePayment(purchasePaymentRequest, purchaseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(purchasePaymentResponse);
    }
}
