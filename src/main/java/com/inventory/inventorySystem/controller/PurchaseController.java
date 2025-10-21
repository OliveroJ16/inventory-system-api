package com.inventory.inventorySystem.controller;

import com.inventory.inventorySystem.dto.request.PurchaseRequest;
import com.inventory.inventorySystem.dto.response.CompletePurchaseResponse;
import com.inventory.inventorySystem.service.interfaces.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<CompletePurchaseResponse> registerPurchase(@RequestBody PurchaseRequest purchaseRequest){
        CompletePurchaseResponse completePurchaseResponse = purchaseService.registerPurchase(purchaseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(completePurchaseResponse);
    }
}
